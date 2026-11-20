-- Nexus. Instale por 00_instalar.sql em modo SQLCMD, com parada em erro.
-- Instalação não destrutiva: execute em uma base nova.
IF DB_ID(N'NexoImob') IS NULL CREATE DATABASE NexoImob;
GO
USE NexoImob;
GO
IF OBJECT_ID(N'dbo.usuarios',N'U') IS NOT NULL THROW 51000, 'A base já contém o projeto. Use uma base nova ou migrações.', 1;
GO
CREATE SEQUENCE dbo.seq_ids AS BIGINT START WITH 1 INCREMENT BY 1;
GO
CREATE TABLE dbo.usuarios(
    id BIGINT NOT NULL CONSTRAINT PK_usuarios PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    nome NVARCHAR(120) NOT NULL,
    email NVARCHAR(180) NOT NULL,
    senha_hash NVARCHAR(100) NOT NULL,
    perfil NVARCHAR(20) NOT NULL,
    ativo BIT NOT NULL,
    CONSTRAINT UQ_usuarios_email UNIQUE (email),
    CONSTRAINT CK_usuarios_perfil CHECK (perfil IN (N'ADMIN',N'CORRETOR'))
);
GO
CREATE TABLE dbo.leads(
    id BIGINT NOT NULL CONSTRAINT PK_leads PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    nome NVARCHAR(120) NOT NULL,
    email NVARCHAR(180) NULL,
    telefone NVARCHAR(30) NOT NULL,
    origem NVARCHAR(30) NOT NULL,
    etapa NVARCHAR(30) NOT NULL,
    orcamento DECIMAL(19,2) NOT NULL,
    observacoes NVARCHAR(2000) NULL,
    motivo_perda NVARCHAR(200) NULL,
    criado_em DATETIME2(6) NOT NULL,
    primeiro_contato_em DATETIME2(6) NULL,
    encerrado_em DATETIME2(6) NULL,
    responsavel_id BIGINT NOT NULL,
    atualizado_por_id BIGINT NOT NULL,
    CONSTRAINT CK_leads_origem CHECK (origem IN (N'INDICACAO',N'PORTAL',N'REDES_SOCIAIS',N'SITE',N'OUTRO')),
    CONSTRAINT CK_leads_etapa CHECK (etapa IN (N'NOVO',N'CONTATADO',N'QUALIFICADO',N'VISITA',N'PROPOSTA',N'GANHO',N'PERDIDO')),
    CONSTRAINT CK_leads_orcamento CHECK (orcamento>=0),
    CONSTRAINT FK_leads_responsavel_id FOREIGN KEY (responsavel_id) REFERENCES dbo.usuarios(id),
    CONSTRAINT FK_leads_atualizado_por_id FOREIGN KEY (atualizado_por_id) REFERENCES dbo.usuarios(id),
    CONSTRAINT CK_leads_datas CHECK ((primeiro_contato_em IS NULL OR primeiro_contato_em>=criado_em) AND (encerrado_em IS NULL OR encerrado_em>=criado_em)),
    CONSTRAINT CK_leads_perda CHECK (etapa<>N'PERDIDO' OR (motivo_perda IS NOT NULL AND motivo_perda IN(N'ORCAMENTO',N'SEM_RETORNO',N'IMOVEL_INADEQUADO',N'DOCUMENTACAO',N'OUTRO'))),
    CONSTRAINT CK_leads_fechamento CHECK ((etapa IN(N'GANHO',N'PERDIDO') AND encerrado_em IS NOT NULL) OR (etapa NOT IN(N'GANHO',N'PERDIDO') AND encerrado_em IS NULL))
);
GO
CREATE TABLE dbo.imoveis(
    id BIGINT NOT NULL CONSTRAINT PK_imoveis PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    titulo NVARCHAR(160) NOT NULL,
    tipo NVARCHAR(30) NOT NULL,
    finalidade NVARCHAR(20) NOT NULL,
    cidade NVARCHAR(100) NOT NULL,
    bairro NVARCHAR(100) NOT NULL,
    quartos INT NOT NULL,
    area DECIMAL(12,2) NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    situacao NVARCHAR(30) NOT NULL,
    descricao NVARCHAR(2000) NULL,
    criado_por_id BIGINT NOT NULL,
    CONSTRAINT CK_imoveis_tipo CHECK (tipo IN (N'CASA',N'APARTAMENTO',N'TERRENO',N'COMERCIAL')),
    CONSTRAINT CK_imoveis_finalidade CHECK (finalidade IN (N'VENDA',N'LOCACAO')),
    CONSTRAINT CK_imoveis_quartos CHECK (quartos>=0),
    CONSTRAINT CK_imoveis_area CHECK (area>=0),
    CONSTRAINT CK_imoveis_valor CHECK (valor>=0),
    CONSTRAINT CK_imoveis_situacao CHECK (situacao IN (N'DISPONIVEL',N'RESERVADO',N'NEGOCIADO',N'INATIVO')),
    CONSTRAINT FK_imoveis_criado_por_id FOREIGN KEY (criado_por_id) REFERENCES dbo.usuarios(id)
);
GO
CREATE TABLE dbo.interesses(
    id BIGINT NOT NULL CONSTRAINT PK_interesses PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    lead_id BIGINT NOT NULL,
    imovel_id BIGINT NOT NULL,
    criado_em DATETIME2(6) NOT NULL,
    CONSTRAINT FK_interesses_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE,
    CONSTRAINT FK_interesses_imovel_id FOREIGN KEY (imovel_id) REFERENCES dbo.imoveis(id),
    CONSTRAINT UQ_interesses_lead_imovel UNIQUE(lead_id,imovel_id)
);
GO
CREATE TABLE dbo.atividades(
    id BIGINT NOT NULL CONSTRAINT PK_atividades PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    lead_id BIGINT NOT NULL,
    titulo NVARCHAR(160) NOT NULL,
    tipo NVARCHAR(30) NOT NULL,
    prazo DATETIME2(6) NOT NULL,
    concluida_em DATETIME2(6) NULL,
    observacoes NVARCHAR(2000) NULL,
    CONSTRAINT FK_atividades_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE,
    CONSTRAINT CK_atividades_tipo CHECK (tipo IN (N'LIGACAO',N'MENSAGEM',N'DOCUMENTACAO',N'OUTRO'))
);
GO
CREATE TABLE dbo.visitas(
    id BIGINT NOT NULL CONSTRAINT PK_visitas PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    lead_id BIGINT NOT NULL,
    imovel_id BIGINT NOT NULL,
    agendada_em DATETIME2(6) NOT NULL,
    situacao NVARCHAR(30) NOT NULL,
    virtual BIT NOT NULL,
    km DECIMAL(10,2) NOT NULL,
    observacoes NVARCHAR(2000) NULL,
    CONSTRAINT FK_visitas_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE,
    CONSTRAINT FK_visitas_imovel_id FOREIGN KEY (imovel_id) REFERENCES dbo.imoveis(id),
    CONSTRAINT FK_visitas_interesse FOREIGN KEY (lead_id,imovel_id) REFERENCES dbo.interesses(lead_id,imovel_id),
    CONSTRAINT CK_visitas_situacao CHECK (situacao IN (N'AGENDADA',N'REALIZADA',N'CANCELADA')),
    CONSTRAINT CK_visitas_km CHECK (km>=0)
);
GO
CREATE TABLE dbo.propostas(
    id BIGINT NOT NULL CONSTRAINT PK_propostas PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    lead_id BIGINT NOT NULL,
    imovel_id BIGINT NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    situacao NVARCHAR(30) NOT NULL,
    criada_em DATETIME2(6) NOT NULL,
    observacoes NVARCHAR(2000) NULL,
    CONSTRAINT FK_propostas_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE,
    CONSTRAINT FK_propostas_imovel_id FOREIGN KEY (imovel_id) REFERENCES dbo.imoveis(id),
    CONSTRAINT FK_propostas_interesse FOREIGN KEY (lead_id,imovel_id) REFERENCES dbo.interesses(lead_id,imovel_id),
    CONSTRAINT CK_propostas_valor CHECK (valor>=0),
    CONSTRAINT CK_propostas_situacao CHECK (situacao IN (N'PENDENTE',N'ACEITA',N'RECUSADA'))
);
GO
CREATE TABLE dbo.ocorrencias(
    id BIGINT NOT NULL CONSTRAINT PK_ocorrencias PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    lead_id BIGINT NOT NULL,
    categoria NVARCHAR(40) NOT NULL,
    descricao NVARCHAR(2000) NOT NULL,
    acao NVARCHAR(2000) NULL,
    criada_em DATETIME2(6) NOT NULL,
    CONSTRAINT FK_ocorrencias_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE,
    CONSTRAINT CK_ocorrencias_categoria CHECK (categoria IN (N'ATRASO_RETORNO',N'INFORMACAO_INCOMPLETA',N'VISITA_CANCELADA',N'FALHA_DOCUMENTACAO',N'OUTRO'))
);
GO
CREATE TABLE dbo.historico_etapas(
    id BIGINT NOT NULL CONSTRAINT PK_historico_etapas PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    lead_id BIGINT NOT NULL,
    etapa_anterior NVARCHAR(30) NULL,
    etapa_nova NVARCHAR(30) NOT NULL,
    alterada_em DATETIME2(6) NOT NULL,
    autor_id BIGINT NOT NULL,
    CONSTRAINT FK_historico_etapas_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE,
    CONSTRAINT FK_historico_etapas_autor_id FOREIGN KEY (autor_id) REFERENCES dbo.usuarios(id)
);
GO
CREATE TABLE dbo.tokens_recuperacao(
    id BIGINT NOT NULL CONSTRAINT PK_tokens_recuperacao PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.seq_ids,
    usuario_id BIGINT NOT NULL,
    token_hash NVARCHAR(64) NOT NULL,
    expira_em DATETIME2(6) NOT NULL,
    usado BIT NOT NULL,
    CONSTRAINT FK_tokens_recuperacao_usuario_id FOREIGN KEY (usuario_id) REFERENCES dbo.usuarios(id),
    CONSTRAINT UQ_tokens_recuperacao_token_hash UNIQUE (token_hash)
);
GO

CREATE INDEX IX_leads_responsavel_etapa ON dbo.leads(responsavel_id,etapa) INCLUDE(criado_em,primeiro_contato_em,encerrado_em);
CREATE INDEX IX_visitas_lead_data ON dbo.visitas(lead_id,agendada_em) INCLUDE(situacao,virtual,km);
CREATE INDEX IX_atividades_lead_prazo ON dbo.atividades(lead_id,prazo) INCLUDE(concluida_em);
CREATE INDEX IX_propostas_lead ON dbo.propostas(lead_id,situacao);
CREATE UNIQUE INDEX UQ_proposta_aceita_lead ON dbo.propostas(lead_id) WHERE situacao=N'ACEITA';
CREATE UNIQUE INDEX UQ_proposta_aceita_imovel ON dbo.propostas(imovel_id) WHERE situacao=N'ACEITA';
GO
