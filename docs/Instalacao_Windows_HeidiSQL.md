# Instalação do Nexus no Windows com HeidiSQL

Este roteiro usa Windows 10/11 de 64 bits, Java 17, SQL Server 2022 Express e
HeidiSQL. O SQL Server armazena os dados; o HeidiSQL é a ferramenta para
administrá-lo. O ZIP completo já inclui o executável Java em `dist/nexus.jar`.

**Se você já tem o banco funcionando:** feche a janela antiga do Java,
extraia o pacote completo em uma pasta própria, preserve suas configurações
locais e siga diretamente a etapa 6. Os dados permanecem no SQL Server.
Não execute a instalação inicial novamente em uma base que já contém o Nexus.

## 1. Baixar os três programas

| Programa | Download oficial | Escolha |
|---|---|---|
| Java / Temurin | [JDK 17](https://adoptium.net/temurin/releases/?version=17&os=windows&arch=x64&package=jdk) | Windows, x64, JDK, instalador MSI |
| SQL Server | [SQL Server 2022 Express](https://www.microsoft.com/en-us/download/details.aspx?id=108838) | `SQL2022-SSEI-Expr.exe` |
| HeidiSQL | [Download](https://www.heidisql.com/download.php) | Windows x64 Installer |

Instale o Java com a opção de adicionar ao **PATH**. Depois, abra um novo
Prompt de Comando e execute `java -version`: deve aparecer Java 17 ou superior
compatível. Instale o HeidiSQL normalmente.

Para executar o pacote completo, Maven, Python, Node.js e SSMS são dispensáveis.
Maven é necessário somente se quiser recompilar o código; Python só participa
das verificações opcionais da pasta `verificacao`.

## 2. Instalar o SQL Server

Execute o instalador do SQL Server e escolha **Básico**. Aceite os termos e
conclua a instalação. Confira o nome da instância: **SQLEXPRESS**. Na tela de
conclusão, o botão **Install SSMS** é opcional; clique em **Close**.

A instalação básica usa sua conta do Windows para administrar a instância.
É por isso que ela pode terminar sem pedir uma senha para o `sa`.

Se você já instalou no modo personalizado, definiu modo misto e sabe a senha
do `sa`, mantenha esses dados e pule a etapa 4.

## 3. Habilitar a conexão TCP/IP

Pressione **Windows + R**, cole o comando abaixo e confirme:

```text
C:\Windows\SysWOW64\SQLServerManager16.msc
```

1. Expanda **Configuração de Rede do SQL Server**, na seção sem “32 bits”.
2. Selecione **Protocolos para SQLEXPRESS**.
3. Clique com o botão direito em **TCP/IP → Habilitar**.
4. Abra as propriedades do TCP/IP; na aba **Protocolo**, confira **Escutar Tudo / Listen All = Sim**.
5. Vá a **Endereços IP → IPAll**, no final da lista.
6. Apague o conteúdo de **Portas TCP Dinâmicas**, deixando o campo vazio.
7. Defina **Porta TCP = 1433** e confirme.
8. Abra **Serviços do SQL Server → SQL Server (SQLEXPRESS) → Reiniciar**.

Se não aparecerem protocolos na direita, confira se selecionou o item
**Protocolos para SQLEXPRESS**, e não a seção de configuração “32 bits”.

## 4. Definir o acesso `sa` pelo HeidiSQL

Esta etapa é para uma instalação básica nova, em que o `sa` ainda não funciona.
Se o acesso com `sa` já está funcionando, vá à etapa 5.

No HeidiSQL, clique em **Nova**, dê o nome **Nexus** e configure:

| Campo | Primeiro acesso |
|---|---|
| Tipo de rede | Microsoft SQL Server (TCP/IP) |
| Biblioteca | Mantenha a biblioteca oferecida pelo instalador |
| Servidor / IP | `127.0.0.1` |
| Porta | `1433` |
| Usar a autenticação do Windows | **Marcado** |
| Senha | Deixe vazia; será usada a conta atual do Windows |

Clique em **Abrir**. Entre com o mesmo usuário Windows que instalou o SQL Server.

1. Carregue `database/heidisql/Configurar_Acesso_SQL_Server.sql` em uma aba **Consulta**.
2. Na linha `DECLARE @senha`, substitua **somente** `TROQUE_AQUI` por uma senha sua, mantendo as aspas.
3. Use pelo menos 12 caracteres, com maiúsculas, minúsculas, números e símbolo. Anote a senha localmente. Se ela contiver uma aspa simples, escreva duas aspas simples nessa posição do SQL.
4. Ative **Parar em erros / Stop on errors in batch mode** na barra do HeidiSQL.
5. Execute o arquivo inteiro com **F9**, sem selecionar um trecho.
6. Após a mensagem de sucesso, reinicie **SQL Server (SQLEXPRESS)** pelo gerenciador.

O script recusa o texto `TROQUE_AQUI` sem alterar a senha. Ele define a senha
do `sa`, habilita esse login e ativa o modo de autenticação Windows + SQL.

No gerenciador de sessões do HeidiSQL, **desmarque a autenticação do Windows**,
preencha **Usuário = sa** e a senha que escolheu e clique em **Abrir**.
Esse é o acesso por usuário e senha que o iniciador do Nexus usará.

## 5. Instalar o banco do Nexus

Extraia o ZIP completo; abra a pasta que contém `README.md`,
`iniciar-sqlserver.bat`, `database` e `dist`. Essa é a raiz do projeto.

Com o Java fechado e a sessão do SQL Server aberta no HeidiSQL:

1. Em **Arquivo → Carregar arquivo SQL**, abra `database/heidisql/Instalar_Nexus_HeidiSQL.sql` em uma aba **Consulta**. Use UTF-8 se perguntar a codificação.
2. Mantenha **Parar em erros** ativado.
3. Execute o arquivo inteiro com **F9** e aguarde o término sem erro.
4. Atualize a árvore de bancos. O nome criado é **NexoImob**, embora a aplicação se chame Nexus.

O instalador já reúne estrutura, funções, views, triggers, procedures e dados
de demonstração. Não execute também os scripts originais 01 a 06.

Para conferir os dados, abra outra consulta, com delimitador normal `;`:

```sql
USE NexoImob;
SELECT COUNT(*) AS usuarios FROM dbo.usuarios;
SELECT COUNT(*) AS atendimentos FROM dbo.leads;
SELECT COUNT(*) AS imoveis FROM dbo.imoveis;
```

Em uma instalação nova com a carga completa, os resultados são **3, 60 e 20**.
Se o banco já recebeu alterações, os números podem ser diferentes.

**Se uma instalação antiga parou no erro 11736:** use somente
`database/heidisql/Concluir_Instalacao_Nexus_HeidiSQL.sql` para carregar os dados
nas tabelas já criadas e ainda vazias. Se ele indicar dados presentes, pare e
confira os cadastros; o script não apaga nem substitui registros.

## 6. Iniciar e acessar

1. Na raiz do projeto, abra **iniciar-sqlserver.bat**.
2. Quando pedir senha, informe **a senha do SQL Server**, definida na etapa 4.
3. Aguarde a mensagem `Started NexoImobApplication`. A janela precisa continuar aberta.
4. Acesse [http://localhost:8080/login](http://localhost:8080/login).
5. Entre com **admin@example.test** e a senha de demonstração **NexoDemo2026!**.

Se a senha de uma conta já foi alterada, use a senha atual. O aplicativo não
redefine usuários existentes ao iniciar. Novos cadastros de corretor precisam
da aprovação de um administrador.

O padrão é `localhost:1433`, banco `NexoImob`, login `sa`. Não é preciso criar
configuração adicional para esse padrão. Se precisar de outra conexão, copie
`configurar-sqlserver.exemplo.bat` para `configurar-sqlserver.local.bat` e ajuste
`DB_URL` e `DB_USER`. Sem `DB_PASSWORD`, o iniciador pede a senha na janela.

## 7. Configurar e testar recuperação de senha

Esta versão pública não tem credenciais de Gmail nem configuração SMTP local.
O envio é opcional: sem ele, o login e os demais cadastros continuam disponíveis.

No [README, na seção Recuperação por e-mail](../README.md), siga as instruções
para copiar `smtp.exemplo.properties` para `smtp.local.properties` e preencher
três campos pelo Bloco de Notas: usuário, senha de app e remetente. Reinicie o
Nexus depois de salvar. Não é preciso modificar código.

Para testar, entre com `admin@example.test` / `NexoDemo2026!`, abra
**Usuários → Novo usuário**, informe um e-mail real, selecione o perfil,
marque **Acesso ativo** e defina uma senha com pelo menos 10 caracteres.
Depois, saia e solicite recuperação para esse e-mail na tela de login.
Os endereços `.test` da carga são fictícios e não recebem mensagens.

O SMTP remetente e o destinatário cadastrado podem ser contas diferentes.
Confira o spam e abra o link no mesmo computador que executa o Nexus.
O token expira em 15 minutos. O arquivo local com a senha de app fica privado.

| Credencial | Onde usar |
|---|---|
| Senha do `sa` | Conexão do HeidiSQL e janela de inicialização do Nexus |
| `NexoDemo2026!` | Login das contas de demonstração em uma base nova |
| Senha de app Google | Somente no arquivo local do SMTP |

## 8. Conferir antes de apresentar

Abra `database/heidisql/Verificar_Nexus_HeidiSQL.sql` em uma aba Consulta e
execute com parada em erros: ele confere os objetos e a consistência básica.
Para testar procedures e triggers em uma base de avaliação, sem outros usuários
trabalhando, execute `database/heidisql/Testar_Fluxos_Nexus_HeidiSQL.sql`.
Os quatro cenários desfazem seus registros; a sequência de IDs pode avançar.

No site, cadastre um cliente, feche o Java, inicie novamente e confira se o
cadastro continua lá. Faça também um envio real de recuperação. Os testes
isolados distribuídos não substituem essas conferências no SQL Server/Gmail.

Para mensagens conhecidas, consulte a tabela **Se algo der errado** no
[README](../README.md). Ao publicar no GitHub, use os fontes públicos, que não
incluem `smtp.local.properties` nem configurações locais com senha.

## Referências oficiais

- [Modo de autenticação do SQL Server](https://learn.microsoft.com/en-us/sql/database-engine/configure-windows/change-server-authentication-mode?view=sql-server-ver16)
- [Porta TCP/IP](https://learn.microsoft.com/en-us/sql/tools/configuration-manager/tcp-ip-properties-ip-addresses-tab?view=sql-server-ver16)
- [Ajuda do HeidiSQL](https://www.heidisql.com/help.php)
- [Senhas de app Google](https://support.google.com/accounts/answer/185833?hl=pt-BR)
- [SMTP Gmail](https://knowledge.workspace.google.com/admin/gmail/send-email-from-a-printer-scanner-or-app?hl=pt-br)
