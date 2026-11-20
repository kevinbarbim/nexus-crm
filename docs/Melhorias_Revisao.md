# Melhorias da revisão de 11/10/2026

O painel considera tarefas pendentes e atrasadas somente de atendimentos abertos. Uma tarefa que ficou sem conclusão quando o atendimento foi encerrado permanece no histórico com o estado `Não concluída` e acesso de consulta.

Cadastro, recuperação de senha, redefinição e perfil apresentam as mensagens de erro no próprio formulário. Nome e email são preservados para correção; senhas precisam ser digitadas novamente. Uma alteração de perfil recusada não modifica o nome salvo no banco. Falhas no envio de email recebem uma mensagem em português.

As alterações usam métodos comuns, `if`, `try/catch` e os serviços existentes. Não foram acrescentadas dependências. O guia do código explica o fluxo entre formulário, controlador, serviço e repositório.

O relatório recebeu referências para as ferramentas de qualidade e a regressão linear, critérios de acompanhamento e um plano de comprovação. Mantém o DER único, a logo, a identificação da equipe e dos docentes. O relatório atual está em A4 vertical; a revisão 0.3.1 gerou 50 páginas, com o sumário preservado.

## Verificações realizadas

- O pacote mantém referências históricas de revisão; a edição atual exige verificação integrada em SQL Server. Os comprovantes da revisão atual estão em Revisao_0_3_1.md.
- Não há comprovação de uma bateria HTTP completa desta edição. Execute o roteiro de demonstração na instância de destino.
- Recuperação de senha conferida com servidor SMTP local: mensagem recebida, redefinição, uso único do link, autenticação com nova senha e tratamento de indisponibilidade.
- Código compilado em Java 17, classes e recursos do JAR conferidos com os arquivos do projeto.
- Todas as páginas do relatório renderizadas e revisadas visualmente; dados e fórmulas da planilha preservados.

## Conferência para a entrega

O script `08_verificacao.sql` confere a estrutura exigida. O novo `09_teste_fluxo.sql` prepara quatro cenários para procedures, views, funções e triggers, desfazendo seus cadastros ao final. Esses scripts ainda precisam ser executados em uma instância SQL Server para comprovar o funcionamento dos objetos T-SQL.

O guia `Conferencia_Entrega.md` reúne os passos para essa execução, persistência após reiniciar o Java, SMTP externo, tarefas com usuários e registros da atividade com o Grupo NexGen. O roteiro e o README orientam a publicação do repositório e a gravação do vídeo. Backup e restauração são a comprovação adicional de bônus.
