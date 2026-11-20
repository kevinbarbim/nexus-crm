-- Opcional: execute apenas se o login sa ainda nao foi configurado.
-- Conecte ao SQLEXPRESS pelo HeidiSQL com autenticacao do Windows.
-- Troque SOMENTE o valor de @senha abaixo; mantenha "Parar em erros" ativo.
-- Este arquivo altera a senha do sa e habilita autenticacao Windows + SQL.

DELIMITER !!
USE [master];

DECLARE @senha NVARCHAR(128) = N'TROQUE_AQUI';

IF @senha = N'TROQUE_AQUI'
    THROW 51130, 'Edite o valor de @senha antes de executar. Nenhuma senha foi alterada.', 1;
IF LEN(@senha) < 12
    THROW 51131, 'Escolha uma senha com pelo menos 12 caracteres.', 1;
IF ISNULL(IS_SRVROLEMEMBER(N'sysadmin'), 0) <> 1
    THROW 51132, 'Entre com a conta Windows administradora definida na instalacao do SQL Server.', 1;

DECLARE @comando NVARCHAR(400) =
    N'ALTER LOGIN [sa] WITH PASSWORD = ' + QUOTENAME(@senha, '''') + N';';
EXEC sys.sp_executesql @comando;
ALTER LOGIN [sa] ENABLE;

EXEC master.dbo.xp_instance_regwrite
    N'HKEY_LOCAL_MACHINE',
    N'Software\Microsoft\MSSQLServer\MSSQLServer',
    N'LoginMode',
    REG_DWORD,
    2;

PRINT N'Acesso configurado. Reinicie SQL Server (SQLEXPRESS) antes de entrar com sa.';
!!
DELIMITER ;
