USE NexoImob;
GO
CREATE OR ALTER PROCEDURE dbo.sp_mover_lead @lead_id BIGINT,@etapa NVARCHAR(30),@motivo NVARCHAR(200),@autor BIGINT AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
      BEGIN TRANSACTION;
      DECLARE @responsavel BIGINT,@atual NVARCHAR(30),@perfil NVARCHAR(20),@ativo BIT;
      SELECT @responsavel=responsavel_id,@atual=etapa FROM dbo.leads WITH(UPDLOCK,HOLDLOCK) WHERE id=@lead_id;
      SELECT @perfil=perfil,@ativo=ativo FROM dbo.usuarios WHERE id=@autor;
      IF @atual IS NULL THROW 51010,'Lead não encontrado.',1;
      IF ISNULL(@ativo,0)=0 OR (@perfil<>N'ADMIN' AND @responsavel<>@autor) THROW 51011,'Operação não autorizada.',1;
      IF @atual IN(N'GANHO',N'PERDIDO') THROW 51012,'Atendimento já encerrado.',1;
      IF @etapa=N'GANHO' THROW 51013,'Use o aceite de proposta para registrar ganho.',1;
      IF @etapa NOT IN(N'NOVO',N'CONTATADO',N'QUALIFICADO',N'VISITA',N'PROPOSTA',N'PERDIDO') THROW 51014,'Etapa inválida.',1;
      IF @etapa=N'PERDIDO' AND (@motivo IS NULL OR @motivo NOT IN(N'ORCAMENTO',N'SEM_RETORNO',N'IMOVEL_INADEQUADO',N'DOCUMENTACAO',N'OUTRO'))
          THROW 51015,'Selecione um motivo de perda válido.',1;
      DECLARE @agora DATETIME2=CONVERT(DATETIME2,SYSDATETIMEOFFSET() AT TIME ZONE 'E. South America Standard Time');
      UPDATE dbo.leads SET etapa=@etapa,atualizado_por_id=@autor,
        primeiro_contato_em=CASE WHEN @etapa<>N'NOVO' THEN COALESCE(primeiro_contato_em,@agora) ELSE primeiro_contato_em END,
        motivo_perda=CASE WHEN @etapa=N'PERDIDO' THEN @motivo ELSE NULL END,
        encerrado_em=CASE WHEN @etapa=N'PERDIDO' THEN @agora ELSE NULL END WHERE id=@lead_id;
      COMMIT;
    END TRY
    BEGIN CATCH
      IF @@TRANCOUNT>0 ROLLBACK;
      THROW;
    END CATCH;
END;
GO
CREATE OR ALTER PROCEDURE dbo.sp_agendar_visita @lead_id BIGINT,@imovel_id BIGINT,@data DATETIME2,@situacao NVARCHAR(30),@virtual BIT,@km DECIMAL(10,2),@observacoes NVARCHAR(2000),@autor BIGINT AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
      BEGIN TRANSACTION;
      DECLARE @responsavel BIGINT,@etapa NVARCHAR(30),@perfil NVARCHAR(20),@ativo BIT;
      SELECT @responsavel=responsavel_id,@etapa=etapa FROM dbo.leads WITH(UPDLOCK,HOLDLOCK) WHERE id=@lead_id;
      SELECT @perfil=perfil,@ativo=ativo FROM dbo.usuarios WHERE id=@autor;
      IF @etapa IS NULL THROW 51020,'Lead não encontrado.',1;
      IF ISNULL(@ativo,0)=0 OR (@perfil<>N'ADMIN' AND @responsavel<>@autor) THROW 51021,'Operação não autorizada.',1;
      IF @etapa IN(N'GANHO',N'PERDIDO') THROW 51022,'Atendimento já encerrado.',1;
      IF NOT EXISTS(SELECT 1 FROM dbo.imoveis WITH(UPDLOCK,HOLDLOCK) WHERE id=@imovel_id AND situacao IN(N'DISPONIVEL',N'RESERVADO')) THROW 51023,'Imóvel indisponível.',1;
      IF NOT EXISTS(SELECT 1 FROM dbo.interesses WHERE lead_id=@lead_id AND imovel_id=@imovel_id) THROW 51024,'Registre o interesse primeiro.',1;
      DECLARE @id BIGINT=NEXT VALUE FOR dbo.seq_ids;
      INSERT dbo.visitas(id,lead_id,imovel_id,agendada_em,situacao,virtual,km,observacoes)
      VALUES(@id,@lead_id,@imovel_id,@data,@situacao,@virtual,@km,@observacoes);
      COMMIT; SELECT @id AS id;
    END TRY
    BEGIN CATCH
      IF @@TRANCOUNT>0 ROLLBACK;
      THROW;
    END CATCH;
END;
GO
CREATE OR ALTER PROCEDURE dbo.sp_aceitar_proposta @proposta_id BIGINT,@autor BIGINT AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
      BEGIN TRANSACTION;
      DECLARE @lead BIGINT,@imovel BIGINT,@situacao NVARCHAR(30),@responsavel BIGINT,@etapa NVARCHAR(30),@perfil NVARCHAR(20),@ativo BIT;
      SELECT @lead=lead_id,@imovel=imovel_id,@situacao=situacao FROM dbo.propostas WITH(UPDLOCK,HOLDLOCK) WHERE id=@proposta_id;
      SELECT @responsavel=responsavel_id,@etapa=etapa FROM dbo.leads WITH(UPDLOCK,HOLDLOCK) WHERE id=@lead;
      SELECT @perfil=perfil,@ativo=ativo FROM dbo.usuarios WHERE id=@autor;
      IF @situacao IS NULL THROW 51030,'Proposta não encontrada.',1;
      IF ISNULL(@ativo,0)=0 OR (@perfil<>N'ADMIN' AND @responsavel<>@autor) THROW 51031,'Operação não autorizada.',1;
      IF @situacao<>N'PENDENTE' OR @etapa IN(N'GANHO',N'PERDIDO') THROW 51032,'Proposta ou atendimento já encerrado.',1;
      IF NOT EXISTS(SELECT 1 FROM dbo.imoveis WITH(UPDLOCK,HOLDLOCK) WHERE id=@imovel AND situacao IN(N'DISPONIVEL',N'RESERVADO')) THROW 51033,'Imóvel indisponível.',1;
      IF EXISTS(SELECT 1 FROM dbo.propostas WHERE situacao=N'ACEITA' AND (lead_id=@lead OR imovel_id=@imovel)) THROW 51034,'Já existe uma proposta aceita.',1;
      DECLARE @agora DATETIME2=CONVERT(DATETIME2,SYSDATETIMEOFFSET() AT TIME ZONE 'E. South America Standard Time');
      UPDATE dbo.propostas SET situacao=N'ACEITA' WHERE id=@proposta_id;
      UPDATE dbo.imoveis SET situacao=N'NEGOCIADO' WHERE id=@imovel;
      UPDATE dbo.leads SET etapa=N'GANHO',primeiro_contato_em=COALESCE(primeiro_contato_em,@agora),encerrado_em=@agora,motivo_perda=NULL,atualizado_por_id=@autor WHERE id=@lead;
      COMMIT;
    END TRY
    BEGIN CATCH
      IF @@TRANCOUNT>0 ROLLBACK;
      THROW;
    END CATCH;
END;
GO
