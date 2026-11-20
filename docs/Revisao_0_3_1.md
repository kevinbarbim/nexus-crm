# Revisão 0.3.1 — 20/11/2026

## Alterações

- Uso exclusivo de SQL Server preservado, sem driver H2.
- Recuperação de senha invalida os demais links pendentes do mesmo usuário.
- Erros de regras SQL 51000–51099 recebem mensagem controlada e status 409;
  falhas gerais de acesso ao banco recebem status 503 e mensagem genérica.
- Falhas SMTP preservam a causa no log do servidor.
- SMTP configurável em arquivo externo, importado automaticamente; credencial
  ausente do código-fonte e do executável.
- Iniciadores usam o fuso de São Paulo e solicitam a senha SQL Server quando
  ela não estiver configurada. O padrão de conexão continua sendo local/sa.
- Administrador de demonstração usa `admin@example.test` na carga inicial.
  A senha do aplicativo permanece `NexoDemo2026!`; uma base existente não é
  alterada automaticamente por uma nova versão do Java.
- Removidos métodos de repositório sem uso e perfil de compilação alternativo.
- Incluídos iniciadores de recompilação que atualizam o JAR em `dist`.
- Corrigidas afirmações de testes no checklist, guia e relatório DOCX/PDF.

## Verificações executadas nesta revisão

As 68 classes principais foram compiladas com source/target 17 usando Java 21
e as dependências já presentes no pacote. O JAR foi atualizado com essas
classes e com os recursos atuais. Essa operação não foi uma execução do Maven.

Foram executadas 34 verificações isoladas tanto sobre as classes compiladas
quanto sobre as classes extraídas do JAR. Abrangem cadastro, hash BCrypt,
estado do login, envio SMTP local, token temporário, uso único, expiração,
recusa de links antigos após redefinição, preservação dos tokens de outro
usuário, vinculação de configurações e tratamento de erro SQL.

Foram capturadas três mensagens por execução em SMTP temporário local.
O código reproduzível está em `verificacao/`; os resultados resumidos estão
em `verificacao/resultado_0_3_1.txt`. As dependências opcionais desse roteiro
são declaradas no README da pasta.

O relatório DOCX foi renderizado para gerar o PDF atualizado, com 50 páginas.
A planilha, os modelos e as capturas da interface foram preservados.

## Verificações que dependem do ambiente de apresentação

- Instalação e execução dos scripts no SQL Server real.
- Fluxo HTTP completo, persistência após reiniciar e concorrência no banco.
- Autenticação e entrega de mensagens reais pelo Gmail.
- Execução de `mvn test`/`mvn package` pelo Maven na máquina da equipe.
- Links reais do repositório e do vídeo, ainda não fornecidos.

As verificações isoladas usam repositórios substitutos em memória. Não são
testes de integração SQL Server e não comprovam as etapas acima.

## Arquivo de configuração privado

A cópia de execução original utilizou `smtp.local.properties` com uma credencial de envio. A distribuição pública contém somente o modelo sem credencial.
O arquivo é ignorado pelo Git. Retire-o de cópias públicas e use
`smtp.exemplo.properties` para que outra pessoa configure a própria conta.

## Revisão de comentários

Foram removidos comentários repetidos, descrições óbvias de classes e
explicações sobre o estilo adotado. Permanecem os comentários que justificam
regras de negócio, segurança e integridade. Nenhuma instrução Java foi
alterada; a revisão foi conferida por comparação lexical e da desassemblagem
das 68 classes recompiladas. Os testes e seus resultados foram preservados.
