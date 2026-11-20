@echo off
rem Opcional: copie para configurar-sqlserver.local.bat se precisar mudar a conexao.
set "DB_URL=jdbc:sqlserver://localhost:1433;databaseName=NexoImob;encrypt=true;trustServerCertificate=true"
set "DB_USER=sa"
rem Sem DB_PASSWORD definido, o iniciador solicita a senha a cada execucao.
rem Os padroes ADMIN_EMAIL e ADMIN_PASSWORD do iniciador valem para base vazia.
rem Consulte README.md antes de personalizar essas variaveis.
