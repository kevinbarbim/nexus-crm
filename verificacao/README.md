# Verificações isoladas da revisão 0.3.2

Opcional, para reproduzir a verificação do executável distribuído:

```bash
python verificar_revisao.py
```

Execute na pasta `verificacao`, ou informe o caminho completo para o script.
Requer Python 3.9+ e JDK 17+; essas ferramentas são necessárias apenas para
este roteiro, não para executar normalmente o Nexus.

O script usa somente a biblioteca padrão do Python e as bibliotecas Java que
já acompanham o JAR. Ele extrai as classes para uma pasta temporária, inicia
uma conferência pelo carregador do JAR e um SMTP em `127.0.0.1`,
executa as verificações e remove os temporários.
Não lê `smtp.local.properties`, não envia mensagens pelo Gmail e não usa H2.

Resultado esperado: `TOTAL_EMPACOTAMENTO=5`, `TOTAL_PASS=34` e três mensagens
capturadas localmente. A primeira etapa confere diretórios, templates,
classes, componentes essenciais e repositórios no próprio JAR.
Ela falha se a descoberta de componentes estiver prejudicada pelo pacote.
As buscas de dados são substitutos em memória, portanto este roteiro não
comprova transações, bloqueios ou persistência no SQL Server, nem o fluxo HTTP.
Para isso, use os scripts SQL 08/09 e `docs/Conferencia_Entrega.md`.

A pasta `src/test/java` mantém separadamente os três testes JUnit de senha,
executados por `mvn test` em uma máquina com JDK e Maven.
