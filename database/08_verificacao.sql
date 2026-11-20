:On Error exit
USE NexoImob;
GO
-- Falha imediatamente se um requisito estrutural estiver ausente.
DECLARE @esperados TABLE(nome SYSNAME, tipo CHAR(2));
INSERT @esperados VALUES
 (N'usuarios','U'),(N'tokens_recuperacao','U'),(N'imoveis','U'),
 (N'leads','U'),(N'historico_etapas','U'),(N'interesses','U'),
 (N'atividades','U'),(N'ocorrencias','U'),(N'propostas','U'),(N'visitas','U'),
 (N'sp_mover_lead','P'),(N'sp_agendar_visita','P'),(N'sp_aceitar_proposta','P'),
 (N'vw_funil_corretor','V'),(N'vw_pareto_perdas','V'),(N'vw_visitas_ambiental','V'),
 (N'trg_leads_historico','TR'),(N'trg_propostas_integridade','TR'),(N'trg_visitas_validacao','TR'),
 (N'fn_tempo_primeiro_contato','FN'),(N'fn_leads_periodo','IF'),(N'fn_resumo_corretor','TF');
IF EXISTS (
    SELECT 1 FROM @esperados e
    LEFT JOIN sys.objects o ON o.name=e.nome AND o.type=e.tipo AND o.schema_id=SCHEMA_ID(N'dbo')
    WHERE o.object_id IS NULL
) THROW 51100, 'Faltam tabelas ou objetos SQL exigidos pelo projeto.', 1;
IF OBJECT_ID(N'dbo.seq_ids',N'SO') IS NULL
    THROW 51101, 'A sequência de identificadores está ausente.', 1;
IF EXISTS (
    SELECT 1 FROM sys.triggers
    WHERE name IN(N'trg_leads_historico',N'trg_propostas_integridade',N'trg_visitas_validacao') AND is_disabled=1
) THROW 51102, 'Um trigger do projeto está desabilitado.', 1;
IF (SELECT COUNT(*) FROM sys.foreign_keys
    WHERE name IN(N'FK_visitas_interesse',N'FK_propostas_interesse')
      AND is_disabled=0 AND is_not_trusted=0)<>2
    THROW 51103, 'As chaves compostas devem estar habilitadas e confiáveis.', 1;
IF (SELECT COUNT(*) FROM sys.foreign_key_columns
    WHERE constraint_object_id IN(OBJECT_ID(N'dbo.FK_visitas_interesse'),OBJECT_ID(N'dbo.FK_propostas_interesse')))<>4
    THROW 51104, 'As chaves de interesse devem conter lead e imóvel.', 1;
IF (SELECT COUNT(*) FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.propostas')
    AND name IN(N'UQ_proposta_aceita_lead',N'UQ_proposta_aceita_imovel')
    AND is_unique=1 AND has_filter=1 AND is_disabled=0)<>2
    THROW 51105, 'Faltam os índices únicos de proposta aceita.', 1;
IF EXISTS (
    SELECT 1 FROM dbo.leads l WHERE l.etapa=N'GANHO'
    AND NOT EXISTS(SELECT 1 FROM dbo.propostas p WHERE p.lead_id=l.id AND p.situacao=N'ACEITA')
) THROW 51106, 'Existe atendimento ganho sem proposta aceita.', 1;
IF EXISTS(SELECT 1 FROM dbo.visitas WHERE virtual=1 AND km<>0)
    THROW 51107, 'Existe visita virtual com quilometragem diferente de zero.', 1;
PRINT N'OK: estrutura, tipos de funções, triggers, chaves compostas, índices e consistência básica.';
GO
SELECT name,type_desc FROM sys.objects WHERE schema_id=SCHEMA_ID('dbo') AND type IN('P','V','TR','FN','IF','TF') ORDER BY type,name;
SELECT * FROM dbo.vw_funil_corretor;
SELECT * FROM dbo.vw_pareto_perdas ORDER BY quantidade DESC,motivo_perda;
SELECT * FROM dbo.vw_visitas_ambiental;
SELECT TOP(10) id,dbo.fn_tempo_primeiro_contato(id) AS horas FROM dbo.leads;
SELECT * FROM dbo.fn_leads_periodo('2026-01-01','2027-01-01',NULL);
SELECT * FROM dbo.fn_resumo_corretor(NULL);
-- Depois de mover um lead pelo Java, confira o trigger de histórico.
SELECT TOP(20) * FROM dbo.historico_etapas ORDER BY id DESC;
GO
