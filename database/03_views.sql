USE NexoImob;
GO
CREATE OR ALTER VIEW dbo.vw_funil_corretor AS
SELECT u.id AS responsavel_id,u.nome AS corretor,l.etapa,COUNT_BIG(*) AS quantidade,
       SUM(l.orcamento) AS orcamento_total,AVG(dbo.fn_tempo_primeiro_contato(l.id)) AS media_horas_contato,
       AVG(CASE WHEN l.encerrado_em IS NOT NULL THEN DATEDIFF_BIG(SECOND,l.criado_em,l.encerrado_em)/86400.0 END) AS media_dias_encerramento
FROM dbo.leads l JOIN dbo.usuarios u ON u.id=l.responsavel_id
GROUP BY u.id,u.nome,l.etapa;
GO
CREATE OR ALTER VIEW dbo.vw_pareto_perdas AS
WITH frequencias AS (
    SELECT motivo_perda,COUNT_BIG(*) AS quantidade FROM dbo.leads WHERE etapa=N'PERDIDO' GROUP BY motivo_perda
)
SELECT motivo_perda,quantidade,
    CAST(100.0*quantidade/NULLIF(SUM(quantidade) OVER(),0) AS DECIMAL(9,4)) AS percentual,
    CAST(100.0*SUM(quantidade) OVER(ORDER BY quantidade DESC,motivo_perda ROWS UNBOUNDED PRECEDING)/NULLIF(SUM(quantidade) OVER(),0) AS DECIMAL(9,4)) AS percentual_acumulado
FROM frequencias;
GO
CREATE OR ALTER VIEW dbo.vw_visitas_ambiental AS
WITH visitas_por_lead AS (
  SELECT lead_id,COUNT_BIG(*) AS realizadas,SUM(CASE WHEN virtual=1 THEN 1 ELSE 0 END) AS virtuais,SUM(km) AS km_realizados
  FROM dbo.visitas WHERE situacao=N'REALIZADA' GROUP BY lead_id
), qualidade AS (
  SELECT lead_id,COUNT_BIG(*) AS ocorrencias FROM dbo.ocorrencias GROUP BY lead_id
)
SELECT l.id AS lead_id,l.responsavel_id,u.nome AS corretor,l.etapa,
       ISNULL(v.realizadas,0) AS visitas_realizadas,ISNULL(v.virtuais,0) AS visitas_virtuais,
       ISNULL(v.km_realizados,0) AS km_realizados,ISNULL(q.ocorrencias,0) AS ocorrencias,
       CAST(100.0*ISNULL(v.virtuais,0)/NULLIF(v.realizadas,0) AS DECIMAL(9,4)) AS percentual_virtual
FROM dbo.leads l JOIN dbo.usuarios u ON u.id=l.responsavel_id
LEFT JOIN visitas_por_lead v ON v.lead_id=l.id LEFT JOIN qualidade q ON q.lead_id=l.id;
GO
