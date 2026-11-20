"""Verificacoes isoladas do JAR. Nao conecta ao SQL Server nem envia email externo."""
from pathlib import Path
import email
import os
import shutil
import socketserver
import subprocess
import sys
import tempfile
import threading
import zipfile

ROOT = Path(__file__).resolve().parent.parent
messages = []


class SMTP(socketserver.StreamRequestHandler):
    def handle(self):
        self.wfile.write(b"220 localhost verificacao SMTP\r\n")
        while True:
            line = self.rfile.readline()
            if not line:
                return
            command = line.decode(errors="replace").upper()
            if command.startswith(("EHLO", "HELO")):
                self.wfile.write(b"250-localhost\r\n250 8BITMIME\r\n")
            elif command.startswith("DATA"):
                self.wfile.write(b"354 End with dot\r\n")
                body = []
                while True:
                    chunk = self.rfile.readline()
                    if chunk in (b".\r\n", b""):
                        break
                    body.append(chunk)
                message = email.message_from_bytes(b"".join(body))
                messages.append(message["To"])
                self.wfile.write(b"250 Accepted locally\r\n")
            elif command.startswith("QUIT"):
                self.wfile.write(b"221 Bye\r\n")
                return
            else:
                self.wfile.write(b"250 OK\r\n")


def main():
    if shutil.which("java") is None:
        print("Instale JDK 17 ou superior para esta verificacao opcional.")
        return 1
    with tempfile.TemporaryDirectory(prefix="nexus-verificacao-") as folder:
        temp = Path(folder)
        with zipfile.ZipFile(ROOT / "dist/nexus.jar") as jar:
            for name in jar.namelist():
                if name.endswith("/"):
                    continue
                if name.startswith("BOOT-INF/lib/"):
                    target = temp / "lib" / Path(name).name
                elif name.startswith("BOOT-INF/classes/"):
                    relative = name.removeprefix("BOOT-INF/classes/")
                    target = temp / "classes" / relative
                else:
                    continue
                if not target.resolve().is_relative_to(temp.resolve()):
                    raise ValueError("Caminho invalido no executavel")
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_bytes(jar.read(name))
        classpath = os.pathsep.join(
            [str(temp / "classes")] + [str(p) for p in (temp / "lib").glob("*.jar")]
        )
        probe_classes = temp / "packaging"
        probe_classes.mkdir()
        compile_probe = subprocess.run(
            ["java", "-m", "jdk.compiler/com.sun.tools.javac.Main",
             "-cp", classpath, "-d", str(probe_classes),
             str(ROOT / "verificacao/VerificarEmpacotamento.java")],
            capture_output=True, text=True, encoding="utf-8", errors="replace"
        )
        if compile_probe.returncode:
            print(compile_probe.stdout)
            print(compile_probe.stderr)
            return compile_probe.returncode
        # Usa o carregador do JAR original; as classes extraidas nao entram aqui.
        packaging = subprocess.run(
            ["java", "-Dloader.path=" + str(probe_classes),
             "-Dloader.main=VerificarEmpacotamento", "-cp", str(ROOT / "dist/nexus.jar"),
             "org.springframework.boot.loader.launch.PropertiesLauncher"],
            capture_output=True, text=True, encoding="utf-8", errors="replace"
        )
        print(packaging.stdout, end="")
        if packaging.returncode:
            print(packaging.stderr)
            return packaging.returncode
        with socketserver.TCPServer(("127.0.0.1", 0), SMTP) as smtp:
            worker = threading.Thread(target=smtp.serve_forever, daemon=True)
            worker.start()
            result = subprocess.run(
                ["java", "-cp", classpath,
                 str(ROOT / "verificacao/VerificarRevisao.java"),
                 str(smtp.server_address[1]),
                 str(ROOT / "database/06_dados_iniciais.sql")],
                capture_output=True, text=True, encoding="utf-8", errors="replace"
            )
            smtp.shutdown()
        for line in result.stdout.splitlines():
            if line.startswith(("PASS ", "TOTAL_PASS=")):
                print(line)
        print("Mensagens capturadas apenas no SMTP local:", len(messages))
        if result.returncode:
            print(result.stdout)
            print(result.stderr)
        return result.returncode


if __name__ == "__main__":
    sys.exit(main())
