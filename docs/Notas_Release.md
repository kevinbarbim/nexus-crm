# Nexus 0.3.2 — entrega acadêmica atualizada

CRM imobiliário em Java 17, Spring Boot e Microsoft SQL Server.

## Download para executar

Baixe **Nexus_v0.3.2_Completo.zip**, anexado em Assets, e extraia. O arquivo inclui
`nexus/dist/nexus.jar`, código-fonte, scripts SQL Server, instalador para HeidiSQL,
planilha, guias e o relatório final **Relatorio_Nexus_ABNT.pdf** de 50 páginas.

Abra `README.md` dentro da pasta `nexus` e siga a instalação. Para usar o
executável, instale Java 17 ou superior compatível e SQL Server; o guia Windows
utiliza HeidiSQL. Maven e Python são necessários apenas para tarefas opcionais
de recompilação e verificação, respectivamente.

## O que mudou

- Relatório final revisado incluído no pacote; versões anteriores do relatório removidas.
- Links do README e informações de publicação alinhados ao repositório e ao vídeo.
- Documentação distingue testes isolados dos relatos de execução no SQL Server e Gmail.
- Versão do projeto e metadados do executável atualizados para 0.3.2.
- Classes Java, telas, bibliotecas, dados e scripts de banco preservados da edição 0.3.1.

## Acesso de demonstração

Em uma base nova com a carga inicial, use `admin@example.test` e `NexoDemo2026!`.
A senha do banco SQL Server é definida por quem instala e é diferente da senha do site.
Uma base existente conserva os usuários e senhas já alterados.

## E-mail e banco

O banco é exclusivamente SQL Server. O pacote não contém credenciais reais.
Para recuperar senhas por e-mail, copie `smtp.exemplo.properties` para
`smtp.local.properties` e preencha manualmente as credenciais da própria conta,
conforme o README. Cadastre um usuário ativo com e-mail real para testar o envio.

## Materiais

- Repositório: https://github.com/kevinbarbim/nexus-crm
- Relatório final: https://github.com/kevinbarbim/nexus-crm/blob/main/documentacao/Relatorio_Nexus_ABNT.pdf
- Vídeo demonstrativo: https://youtu.be/Iw9LiDzFyyQ

Esta atualização preserva a lógica do aplicativo. O pacote foi reconferido com
cinco verificações de empacotamento e 34 verificações isoladas, incluindo três
mensagens em SMTP local. Esses testes não executam transações no SQL Server nem
enviam pelo Gmail; a equipe relatou a execução local do aplicativo e da recuperação.
Siga `docs/Conferencia_Entrega.md` na máquina utilizada para a apresentação.
