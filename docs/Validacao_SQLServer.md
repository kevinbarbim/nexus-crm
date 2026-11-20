# Roteiro de validação — edição SQL Server

**Situação:** a compilação Java foi conferida, mas a execução contra uma instância SQL Server não foi realizada neste ambiente. Não declare sucesso dos testes SQL sem executá-los de fato.

1. Execute `database/00_instalar.sql` em banco novo e confirme que a execução terminou sem erros.
2. Execute `database/08_verificacao.sql` e registre a listagem de tabelas, procedures, views, funções e triggers.
3. Execute `database/09_teste_fluxo.sql`; confira os quatro cenários e suas mensagens OK, sem outros usuários alterando a base.
4. Configure `DB_URL`, `DB_USER`, `DB_PASSWORD`. Inicie o Java com `iniciar-sqlserver.bat` e confirme o login.
5. Cadastre um lead, marque uma atividade, agende uma visita, crie e aceite uma proposta. Verifique os efeitos no SQL Server.
6. Reinicie o Java e confirme que os registros continuam no banco.
7. Para produção de e-mail de recuperação, configure um SMTP autorizado e teste o envio.

**Registre as evidências:** versão do SQL Server, resultado dos scripts 08/09, captura do HeidiSQL com `SELECT COUNT(*) FROM dbo.leads` e resultado de persistência após reiniciar. Execute em base de teste, nunca na base de produção.
