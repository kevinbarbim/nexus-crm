USE NexoImob;
GO
-- 1. Retorno escalar: horas corridas até o primeiro contato.
CREATE OR ALTER FUNCTION dbo.fn_tempo_primeiro_contato(@lead_id BIGINT)
RETURNS DECIMAL(18,4)
AS
BEGIN
    DECLARE @horas DECIMAL(18,4);
    SELECT @horas=CAST(DATEDIFF_BIG(SECOND,criado_em,primeiro_contato_em)/3600.0 AS DECIMAL(18,4))
    FROM dbo.leads WHERE id=@lead_id AND primeiro_contato_em IS NOT NULL;
    RETURN @horas;
END;
GO
-- 2. Retorno TABLE inline. Limite superior exclusivo.
CREATE OR ALTER FUNCTION dbo.fn_leads_periodo(@inicio DATETIME2,@fim DATETIME2,@responsavel BIGINT)
RETURNS TABLE
AS RETURN (
    SELECT l.id,l.responsavel_id,l.origem,l.etapa,l.criado_em,l.encerrado_em,
           dbo.fn_tempo_primeiro_contato(l.id) AS horas_contato
    FROM dbo.leads l
    WHERE l.criado_em>=@inicio AND l.criado_em<@fim
      AND (@responsavel IS NULL OR l.responsavel_id=@responsavel)
);
GO
-- 3. Retorno por variável TABLE, preenchida por mais de uma instrução.
CREATE OR ALTER FUNCTION dbo.fn_resumo_corretor(@responsavel BIGINT)
RETURNS @resumo TABLE(responsavel_id BIGINT,corretor NVARCHAR(120),total_leads INT,ganhos INT,perdas INT,conversao_encerrados DECIMAL(9,4),media_horas_contato DECIMAL(18,4))
AS
BEGIN
    INSERT INTO @resumo
    SELECT u.id,u.nome,COUNT(l.id),
      SUM(CASE WHEN l.etapa=N'GANHO' THEN 1 ELSE 0 END),
      SUM(CASE WHEN l.etapa=N'PERDIDO' THEN 1 ELSE 0 END),NULL,
      AVG(dbo.fn_tempo_primeiro_contato(l.id))
    FROM dbo.usuarios u LEFT JOIN dbo.leads l ON l.responsavel_id=u.id
    WHERE (@responsavel IS NULL OR u.id=@responsavel)
    GROUP BY u.id,u.nome;
    UPDATE @resumo SET conversao_encerrados=CAST(ganhos*1.0/NULLIF(ganhos+perdas,0) AS DECIMAL(9,4));
    RETURN;
END;
GO
