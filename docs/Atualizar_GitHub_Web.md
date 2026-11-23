# Atualizar o Nexus pelo site do GitHub

Esta atualização tem dois ZIPs com finalidades diferentes:

| Arquivo | Uso |
|---|---|
| `Nexus_Atualizar_GitHub_v0.3.2.zip` | Extrair e enviar somente as alterações dos fontes e da documentação ao repositório |
| `Nexus_v0.3.2_Completo.zip` | Anexar fechado à nova Release; contém o aplicativo completo e o JAR |

## 1 Atualizar os arquivos do repositório

1. Baixe e extraia `Nexus_Atualizar_GitHub_v0.3.2.zip`.
2. Abra a pasta **ENVIAR_PARA_O_GITHUB** dentro dele.
3. Abra https://github.com/kevinbarbim/nexus-crm e fique na raiz, na aba **Code**, branch **main**.
4. Clique em **Add file → Upload files**.
5. Selecione todo o CONTEÚDO de `ENVIAR_PARA_O_GITHUB` e arraste para a área de envio.
   Os caminhos devem começar por `README.md`, `pom.xml`, `docs/` e os demais
   arquivos do projeto. Não envie a pasta `ENVIAR_PARA_O_GITHUB` como uma pasta extra.
6. Aguarde terminar. Use a mensagem de commit **Atualiza entrega e documentação para 0.3.2**.
7. Grave na branch `main` com **Commit changes**. Esses arquivos substituem os
   existentes nos mesmos caminhos; os outros arquivos do projeto permanecem no repositório.
8. Confira o título v0.3.2 no README e os links do relatório, planilha e vídeo.

O patch não tem JAR. O executável de cerca de 61 MB pertence à área de Assets
da Release e não deve ser enviado pelo formulário de arquivos do repositório.

## 2 Criar a nova Release

1. Depois do commit acima, abra https://github.com/kevinbarbim/nexus-crm/releases/new.
2. Em **Tag / Select tag**, digite **v0.3.2** e escolha **Create new tag**.
3. Deixe **Target: main** para marcar a versão com os arquivos atualizados.
4. Em **Release title**, coloque **Nexus 0.3.2 — entrega acadêmica atualizada**.
5. Copie o texto de `NOTAS_DA_RELEASE.md`, que acompanha este guia, para **Release notes**.
6. Em **Attach binaries**, anexe **Nexus_v0.3.2_Completo.zip SEM EXTRAIR**.
   Não coloque o ZIP de atualização dos fontes nessa área.
7. Espere o upload terminar. Deixe **Pre-release** desmarcado e marque
   **Set as the latest release**, se essa opção aparecer.
8. Clique em **Publish release**.

A versão v0.3.1 pode permanecer como histórico. O download recomendado passa
a ser o ZIP anexado à v0.3.2. Os itens automáticos `Source code (zip)` e
`Source code (tar.gz)` são fontes do GitHub e não incluem o executável.

## 3 Conferir a publicação

1. Abra a Release v0.3.2 e baixe o ZIP em Assets.
2. Extraia e confira `nexus/dist/nexus.jar` e
   `nexus/documentacao/Relatorio_Nexus_ABNT.pdf`.
3. Para rodar, siga o README e use `iniciar-sqlserver.bat` no Windows.
4. Confira que o README da branch `main` também abre o relatório e o vídeo.

O relatório final está dentro do novo ZIP; não é necessário anexar uma segunda
cópia dele à Release. A configuração SMTP continua manual e sem credenciais reais.

Referência oficial: https://docs.github.com/pt/repositories/releasing-projects-on-github/managing-releases-in-a-repository
