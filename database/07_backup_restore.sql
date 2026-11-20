-- Bônus previsto no modelo. Ajuste o caminho para uma pasta acessível pelo serviço SQL Server.
-- Execute com permissão de backup/restore. A restauração usa OUTRO banco e não sobrescreve NexoImob.
USE master;
GO
BACKUP DATABASE NexoImob TO DISK=N'C:\SQLBackups\NexoImob.bak' WITH COPY_ONLY,CHECKSUM,STATS=10;
GO
RESTORE VERIFYONLY FROM DISK=N'C:\SQLBackups\NexoImob.bak' WITH CHECKSUM;
GO
RESTORE FILELISTONLY FROM DISK=N'C:\SQLBackups\NexoImob.bak';
GO
-- Confira os nomes lógicos acima antes de executar. Os caminhos também precisam existir.
IF DB_ID(N'NexoImob_Restaurado') IS NOT NULL THROW 51080,'Escolha um nome novo para a base de restauração.',1;
RESTORE DATABASE NexoImob_Restaurado FROM DISK=N'C:\SQLBackups\NexoImob.bak'
WITH MOVE N'NexoImob' TO N'C:\SQLData\NexoImob_Restaurado.mdf',
     MOVE N'NexoImob_log' TO N'C:\SQLData\NexoImob_Restaurado_log.ldf',CHECKSUM,STATS=10;
GO
SELECT 'Original' AS base,COUNT_BIG(*) AS leads FROM NexoImob.dbo.leads
UNION ALL SELECT 'Restaurada',COUNT_BIG(*) FROM NexoImob_Restaurado.dbo.leads;
GO
DBCC CHECKDB(N'NexoImob_Restaurado') WITH NO_INFOMSGS;
GO
