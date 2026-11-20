USE NexoImob;
GO
-- Processa conjuntos, inclusive INSERT/UPDATE de várias linhas.
CREATE OR ALTER TRIGGER dbo.trg_leads_historico ON dbo.leads AFTER INSERT,UPDATE,DELETE AS
BEGIN
    SET NOCOUNT ON;
    -- Atendimento encerrado preserva a etapa e não pode ser excluído.
    IF EXISTS(SELECT 1 FROM deleted d LEFT JOIN inserted i ON i.id=d.id
              WHERE d.etapa IN(N'GANHO',N'PERDIDO') AND (i.id IS NULL OR i.etapa<>d.etapa))
        THROW 51007,'Atendimento encerrado não pode ser reaberto ou excluído.',1;
    IF EXISTS(SELECT 1 FROM inserted i LEFT JOIN dbo.propostas p ON p.lead_id=i.id AND p.situacao=N'ACEITA' WHERE i.etapa=N'GANHO' AND p.id IS NULL)
        THROW 51001,'Lead ganho exige proposta aceita.',1;
    INSERT dbo.historico_etapas(lead_id,etapa_anterior,etapa_nova,alterada_em,autor_id)
    SELECT i.id,d.etapa,i.etapa,CONVERT(DATETIME2,SYSDATETIMEOFFSET() AT TIME ZONE 'E. South America Standard Time'),i.atualizado_por_id
    FROM inserted i LEFT JOIN deleted d ON d.id=i.id WHERE d.id IS NULL OR i.etapa<>d.etapa;
END;
GO
CREATE OR ALTER TRIGGER dbo.trg_propostas_integridade ON dbo.propostas AFTER INSERT,UPDATE,DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted i LEFT JOIN dbo.interesses x ON x.lead_id=i.lead_id AND x.imovel_id=i.imovel_id WHERE x.id IS NULL)
        THROW 51002,'Proposta exige imóvel vinculado aos interesses do lead.',1;
    IF EXISTS(SELECT 1 FROM deleted d LEFT JOIN inserted i ON i.id=d.id
              WHERE d.situacao=N'ACEITA' AND (i.id IS NULL OR i.situacao<>d.situacao
                OR i.valor<>d.valor OR i.lead_id<>d.lead_id OR i.imovel_id<>d.imovel_id
                OR i.criada_em<>d.criada_em OR ISNULL(i.observacoes,N'')<>ISNULL(d.observacoes,N'')))
        THROW 51003,'Proposta aceita não pode ser alterada ou excluída.',1;
    IF EXISTS(SELECT 1 FROM deleted d JOIN dbo.leads l ON l.id=d.lead_id WHERE l.etapa IN(N'GANHO',N'PERDIDO'))
        THROW 51008,'Proposta de atendimento encerrado não pode ser alterada ou excluída.',1;
    IF EXISTS(SELECT 1 FROM inserted i LEFT JOIN deleted d ON d.id=i.id JOIN dbo.imoveis m ON m.id=i.imovel_id
              WHERE i.situacao=N'ACEITA' AND (d.id IS NULL OR d.situacao<>N'ACEITA') AND m.situacao NOT IN(N'DISPONIVEL',N'RESERVADO'))
        THROW 51004,'Imóvel indisponível para aceitar proposta.',1;
END;
GO
CREATE OR ALTER TRIGGER dbo.trg_visitas_validacao ON dbo.visitas AFTER INSERT,UPDATE,DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM deleted d JOIN dbo.leads l ON l.id=d.lead_id WHERE l.etapa IN(N'GANHO',N'PERDIDO'))
        THROW 51009,'Visita de atendimento encerrado não pode ser alterada ou excluída.',1;
    IF EXISTS(SELECT 1 FROM inserted i JOIN dbo.leads l ON l.id=i.lead_id WHERE l.etapa IN(N'GANHO',N'PERDIDO'))
        THROW 51016,'Agende a visita em um atendimento aberto.',1;
    IF EXISTS(SELECT 1 FROM inserted WHERE virtual=1 AND km<>0) THROW 51005,'Visita virtual deve registrar zero quilômetros.',1;
    IF EXISTS(SELECT 1 FROM inserted i LEFT JOIN dbo.interesses x ON x.lead_id=i.lead_id AND x.imovel_id=i.imovel_id WHERE x.id IS NULL)
        THROW 51006,'Visita exige imóvel vinculado aos interesses do lead.',1;
END;
GO
