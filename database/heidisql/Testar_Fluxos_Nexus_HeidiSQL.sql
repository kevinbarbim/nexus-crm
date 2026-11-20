-- Nexus: quatro cenarios SQL em base de avaliacao, com ROLLBACK dos registros.
-- Abra em uma aba Consulta do HeidiSQL e execute com F9.
-- Mantenha Parar em erros ativo.

DELIMITER !!
USE NexoImob;
!!
SET NOCOUNT ON;
-- Executar em uma base de avaliação, sem outros usuários trabalhando nela.
-- Cada cenário tem sua própria transação. Os registros são desfeitos ao final.
-- A sequência pode avançar após ROLLBACK, comportamento normal do SQL Server.
DECLARE @caso INT=1;
WHILE @caso<=4
BEGIN
    DECLARE @esperado INT=CASE @caso WHEN 2 THEN 51001 WHEN 3 THEN 51005 WHEN 4 THEN 51003 ELSE 0 END;
    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @usuario BIGINT=NEXT VALUE FOR dbo.seq_ids;
        DECLARE @lead BIGINT=NEXT VALUE FOR dbo.seq_ids;
        DECLARE @imovel BIGINT=NEXT VALUE FOR dbo.seq_ids;
        DECLARE @proposta BIGINT=NEXT VALUE FOR dbo.seq_ids;
        DECLARE @agora DATETIME2(6)=CONVERT(DATETIME2,SYSDATETIMEOFFSET() AT TIME ZONE 'E. South America Standard Time');
        DECLARE @inicio DATETIME2(6)=DATEADD(HOUR,-24,@agora);
        DECLARE @fim DATETIME2(6)=DATEADD(DAY,1,@agora);
        INSERT dbo.usuarios(id,nome,email,senha_hash,perfil,ativo)
        VALUES(@usuario,N'Verificação SQL',CONCAT(N'verificacao.',NEWID(),N'@example.test'),N'conta-sem-acesso',N'ADMIN',1);
        INSERT dbo.leads(id,nome,telefone,origem,etapa,orcamento,criado_em,responsavel_id,atualizado_por_id)
        VALUES(@lead,N'Atendimento de verificação',N'17999999999',N'SITE',N'NOVO',300000,@inicio,@usuario,@usuario);
        INSERT dbo.imoveis(id,titulo,tipo,finalidade,cidade,bairro,quartos,area,valor,situacao,criado_por_id)
        VALUES(@imovel,N'Unidade de verificação',N'CASA',N'VENDA',N'São José do Rio Preto',N'Centro',2,80,300000,N'DISPONIVEL',@usuario);
        INSERT dbo.interesses(lead_id,imovel_id,criado_em) VALUES(@lead,@imovel,@agora);
        INSERT dbo.propostas(id,lead_id,imovel_id,valor,situacao,criada_em)
        VALUES(@proposta,@lead,@imovel,290000,N'PENDENTE',@agora);
        IF @caso=1
        BEGIN
            IF NOT EXISTS(SELECT 1 FROM dbo.historico_etapas WHERE lead_id=@lead AND etapa_anterior IS NULL AND etapa_nova=N'NOVO')
                THROW 51110, 'O cadastro não gerou histórico.', 1;
            EXEC dbo.sp_mover_lead @lead_id=@lead,@etapa=N'CONTATADO',@motivo=NULL,@autor=@usuario;
            IF NOT EXISTS(SELECT 1 FROM dbo.leads WHERE id=@lead AND etapa=N'CONTATADO' AND primeiro_contato_em IS NOT NULL)
                THROW 51111, 'A mudança de etapa não registrou o contato.', 1;
            IF dbo.fn_tempo_primeiro_contato(@lead) IS NULL OR dbo.fn_tempo_primeiro_contato(@lead)<24
                THROW 51112, 'A função escalar retornou um intervalo incorreto.', 1;
            EXEC dbo.sp_agendar_visita @lead_id=@lead,@imovel_id=@imovel,@data=@agora,
                @situacao=N'REALIZADA',@virtual=1,@km=0,@observacoes=N'Verificação de visita',@autor=@usuario;
            IF NOT EXISTS(SELECT 1 FROM dbo.vw_visitas_ambiental WHERE lead_id=@lead AND visitas_realizadas=1 AND visitas_virtuais=1 AND km_realizados=0)
                THROW 51113, 'A view ambiental não refletiu a visita.', 1;
            IF NOT EXISTS(SELECT 1 FROM dbo.fn_leads_periodo(@inicio,@fim,@usuario) WHERE id=@lead)
                THROW 51114, 'A função inline não retornou o atendimento.', 1;
            EXEC dbo.sp_aceitar_proposta @proposta_id=@proposta,@autor=@usuario;
            IF NOT EXISTS(SELECT 1 FROM dbo.propostas WHERE id=@proposta AND situacao=N'ACEITA')
                THROW 51115, 'A proposta não foi aceita.', 1;
            IF NOT EXISTS(SELECT 1 FROM dbo.leads WHERE id=@lead AND etapa=N'GANHO' AND encerrado_em IS NOT NULL)
                THROW 51116, 'O atendimento não foi encerrado com ganho.', 1;
            IF NOT EXISTS(SELECT 1 FROM dbo.imoveis WHERE id=@imovel AND situacao=N'NEGOCIADO')
                THROW 51117, 'O imóvel não foi marcado como negociado.', 1;
            IF NOT EXISTS(SELECT 1 FROM dbo.historico_etapas WHERE lead_id=@lead AND etapa_nova=N'GANHO' AND autor_id=@usuario)
                THROW 51118, 'O aceite não gerou histórico com o autor.', 1;
            IF NOT EXISTS(SELECT 1 FROM dbo.fn_resumo_corretor(@usuario) WHERE total_leads=1 AND ganhos=1 AND conversao_encerrados=1)
                THROW 51119, 'A função TABLE não refletiu o aceite.', 1;
            IF NOT EXISTS(SELECT 1 FROM dbo.vw_funil_corretor WHERE responsavel_id=@usuario AND etapa=N'GANHO' AND quantidade=1)
                THROW 51120, 'A view do funil não refletiu o ganho.', 1;
            DECLARE @perda BIGINT=NEXT VALUE FOR dbo.seq_ids;
            INSERT dbo.leads(id,nome,telefone,origem,etapa,orcamento,criado_em,responsavel_id,atualizado_por_id)
            VALUES(@perda,N'Perda de verificação',N'17999999999',N'SITE',N'NOVO',300000,@inicio,@usuario,@usuario);
            DECLARE @antes BIGINT=ISNULL((SELECT quantidade FROM dbo.vw_pareto_perdas WHERE motivo_perda=N'DOCUMENTACAO'),0);
            EXEC dbo.sp_mover_lead @lead_id=@perda,@etapa=N'PERDIDO',@motivo=N'DOCUMENTACAO',@autor=@usuario;
            IF NOT EXISTS(SELECT 1 FROM dbo.vw_pareto_perdas WHERE motivo_perda=N'DOCUMENTACAO' AND quantidade=@antes+1)
                THROW 51121, 'A view do Pareto não refletiu a perda.', 1;
            ROLLBACK TRANSACTION;
            PRINT N'OK 1: três procedures, três views, três funções, histórico e aceite atômico.';
        END
        ELSE
        BEGIN
            IF @caso=2
                UPDATE dbo.leads SET etapa=N'GANHO',encerrado_em=@agora WHERE id=@lead;
            IF @caso=3
                INSERT dbo.visitas(lead_id,imovel_id,agendada_em,situacao,virtual,km)
                VALUES(@lead,@imovel,@agora,N'AGENDADA',1,1);
            IF @caso=4
            BEGIN
                EXEC dbo.sp_aceitar_proposta @proposta_id=@proposta,@autor=@usuario;
                UPDATE dbo.propostas SET valor=valor+1 WHERE id=@proposta;
            END
            THROW 51122, 'Uma operação inválida foi aceita pelo trigger.', 1;
        END
    END TRY
    BEGIN CATCH
        DECLARE @erro INT=ERROR_NUMBER();
        IF @@TRANCOUNT>0 ROLLBACK TRANSACTION;
        IF @esperado=0 OR @erro<>@esperado THROW;
        PRINT CONCAT(N'OK ',@caso,N': operação recusada pelo trigger, erro ',@erro,N'.');
    END CATCH;
    SET @caso=@caso+1;
END;
PRINT N'VERIFICAÇÃO CONCLUÍDA. Nenhum cadastro deste roteiro permanece gravado.';
!!
DELIMITER ;
