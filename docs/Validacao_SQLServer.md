# Roteiro de validação em SQL Server

A equipe confirmou a execução local do Nexus com SQL Server e recuperação de
senha por Gmail. A preparação automatizada do pacote utiliza testes isolados,
sem executar uma instância SQL Server ou envio pelo Gmail. Use este roteiro
para conferir a instalação na máquina da apresentação e preservar as evidências.

1. Em banco novo, instale pelo `database/heidisql/Instalar_Nexus_HeidiSQL.sql`.
   Se utilizar SQLCMD, o caminho alternativo é `database/00_instalar.sql`.
2. No HeidiSQL, execute `database/heidisql/Verificar_Nexus_HeidiSQL.sql` e registre os objetos encontrados.
3. Em base de avaliação, execute `database/heidisql/Testar_Fluxos_Nexus_HeidiSQL.sql` e confira os quatro cenários.
4. Inicie o Nexus e confirme os acessos de administrador e corretor.
5. Cadastre um atendimento, altere a etapa, agende visita, crie e aceite proposta.
6. Reinicie o Java e confirme os mesmos registros no sistema e no HeidiSQL.
7. Para demonstrar recuperação, configure o SMTP manualmente e use um usuário ativo com e-mail real.
8. Para o bônus de backup/restore, siga `database/07_backup_restore.sql` e guarde o resultado da restauração.

As versões SQLCMD dos scripts de conferência são `08_verificacao.sql` e
`09_teste_fluxo.sql`. Não execute a carga inicial novamente sobre uma base Nexus já preenchida.
