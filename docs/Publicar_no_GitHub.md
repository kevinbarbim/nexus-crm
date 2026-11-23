# Publicar e atualizar o Nexus pelo site do GitHub

O repositório público já está em https://github.com/kevinbarbim/nexus-crm. Para publicar a versão 0.3.2,
siga o [passo a passo de atualização pelo navegador](Atualizar_GitHub_Web.md).

Primeiro envie os arquivos da pasta `ENVIAR_PARA_O_GITHUB` para a branch `main`.
Depois crie a tag `v0.3.2` e anexe `Nexus_v0.3.2_Completo.zip` à Release.
Os detalhes da versão estão em [Notas da Release](Notas_Release.md).

O ZIP executável tem o relatório final, código, instalador HeidiSQL, scripts,
planilha e `dist/nexus.jar`. Os fontes automáticos do GitHub não incluem esse JAR.
As credenciais de banco e e-mail são configuradas por quem instala. A configuração
de SMTP é manual, conforme o README; os arquivos locais com senhas ficam fora do Git.

Se criar outro repositório, envie os fontes sem JAR e sem credenciais à raiz,
mantendo os caminhos `README.md`, `src/`, `database/`, `docs/` e `documentacao/`.
O envio pelo navegador permite até 100 arquivos de cada vez; divida um projeto
completo em lotes quando necessário. A atualização 0.3.2 tem menos de 100 arquivos.

Referências oficiais:
- https://docs.github.com/pt/repositories/working-with-files/managing-files/adding-a-file-to-a-repository
- https://docs.github.com/pt/repositories/releasing-projects-on-github/managing-releases-in-a-repository
