# Conferência antes da apresentação

1. Siga o [guia Windows](Instalacao_Windows_HeidiSQL.md) e confirme a tela do Nexus.
2. No HeidiSQL, execute `database/heidisql/Verificar_Nexus_HeidiSQL.sql` com parada em erros. Confira a mensagem OK para estrutura, objetos e consistência básica.
3. Em uma base de avaliação, sem outras pessoas trabalhando, execute `database/heidisql/Testar_Fluxos_Nexus_HeidiSQL.sql`. Confira OK nos quatro cenários e a mensagem de conclusão; os registros criados pelo roteiro são desfeitos.
4. Entre no site com o administrador, cadastre um cliente, altere um dado e guarde o ID. Pare o Java, inicie novamente e abra a ficha para conferir persistência.
5. Consulte o mesmo ID em `dbo.leads` no HeidiSQL. Demonstre também o histórico e o fluxo de visita/proposta conforme o roteiro da apresentação.
6. Solicite recuperação para um usuário ativo com e-mail real, confira a mensagem na caixa de entrada ou spam e abra o link no mesmo PC do Nexus. Depois da troca, confirme o novo login.

As contas corretor da carga têm endereços `.test`, que não recebem e-mails.
Pelo administrador, crie em Usuários → Novo usuário uma conta ativa com e-mail real para testar o recebimento.

Registre versão do SQL Server, mensagens dos scripts e capturas com os IDs.
As verificações isoladas da pasta `verificacao` conferem componentes e regras
com substitutos; persistência, transações SQL e Gmail exigem estes testes reais.
Não exponha senhas ou tokens nas capturas e no vídeo.
