# Dicionário de dados — Nexus

Referência: `database/01_schema.sql`. IDs usam a sequência BIGINT `dbo.seq_ids`.

## usuarios

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| nome | NVARCHAR(120) | Sim |
| email | NVARCHAR(180) | Sim |
| senha_hash | NVARCHAR(100) | Sim |
| perfil | NVARCHAR(20) | Sim |
| ativo | BIT | Sim |

Restrições:

```sql
CONSTRAINT UQ_usuarios_email UNIQUE (email)
CONSTRAINT CK_usuarios_perfil CHECK (perfil IN (N'ADMIN',N'CORRETOR'))
```

## leads

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| nome | NVARCHAR(120) | Sim |
| email | NVARCHAR(180) | Não |
| telefone | NVARCHAR(30) | Sim |
| origem | NVARCHAR(30) | Sim |
| etapa | NVARCHAR(30) | Sim |
| orcamento | DECIMAL(19,2) | Sim |
| observacoes | NVARCHAR(2000) | Não |
| motivo_perda | NVARCHAR(200) | Não |
| criado_em | DATETIME2(6) | Sim |
| primeiro_contato_em | DATETIME2(6) | Não |
| encerrado_em | DATETIME2(6) | Não |
| responsavel_id | BIGINT | Sim |
| atualizado_por_id | BIGINT | Sim |

Restrições:

```sql
CONSTRAINT CK_leads_origem CHECK (origem IN (N'INDICACAO',N'PORTAL',N'REDES_SOCIAIS',N'SITE',N'OUTRO'))
CONSTRAINT CK_leads_etapa CHECK (etapa IN (N'NOVO',N'CONTATADO',N'QUALIFICADO',N'VISITA',N'PROPOSTA',N'GANHO',N'PERDIDO'))
CONSTRAINT CK_leads_orcamento CHECK (orcamento>=0)
CONSTRAINT FK_leads_responsavel_id FOREIGN KEY (responsavel_id) REFERENCES dbo.usuarios(id)
CONSTRAINT FK_leads_atualizado_por_id FOREIGN KEY (atualizado_por_id) REFERENCES dbo.usuarios(id)
CONSTRAINT CK_leads_datas CHECK ((primeiro_contato_em IS NULL OR primeiro_contato_em>=criado_em) AND (encerrado_em IS NULL OR encerrado_em>=criado_em))
CONSTRAINT CK_leads_perda CHECK (etapa<>N'PERDIDO' OR (motivo_perda IS NOT NULL AND motivo_perda IN(N'ORCAMENTO',N'SEM_RETORNO',N'IMOVEL_INADEQUADO',N'DOCUMENTACAO',N'OUTRO')))
CONSTRAINT CK_leads_fechamento CHECK ((etapa IN(N'GANHO',N'PERDIDO') AND encerrado_em IS NOT NULL) OR (etapa NOT IN(N'GANHO',N'PERDIDO') AND encerrado_em IS NULL))
```

## imoveis

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| titulo | NVARCHAR(160) | Sim |
| tipo | NVARCHAR(30) | Sim |
| finalidade | NVARCHAR(20) | Sim |
| cidade | NVARCHAR(100) | Sim |
| bairro | NVARCHAR(100) | Sim |
| quartos | INT | Sim |
| area | DECIMAL(12,2) | Sim |
| valor | DECIMAL(19,2) | Sim |
| situacao | NVARCHAR(30) | Sim |
| descricao | NVARCHAR(2000) | Não |
| criado_por_id | BIGINT | Sim |

Restrições:

```sql
CONSTRAINT CK_imoveis_tipo CHECK (tipo IN (N'CASA',N'APARTAMENTO',N'TERRENO',N'COMERCIAL'))
CONSTRAINT CK_imoveis_finalidade CHECK (finalidade IN (N'VENDA',N'LOCACAO'))
CONSTRAINT CK_imoveis_quartos CHECK (quartos>=0)
CONSTRAINT CK_imoveis_area CHECK (area>=0)
CONSTRAINT CK_imoveis_valor CHECK (valor>=0)
CONSTRAINT CK_imoveis_situacao CHECK (situacao IN (N'DISPONIVEL',N'RESERVADO',N'NEGOCIADO',N'INATIVO'))
CONSTRAINT FK_imoveis_criado_por_id FOREIGN KEY (criado_por_id) REFERENCES dbo.usuarios(id)
```

## interesses

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| lead_id | BIGINT | Sim |
| imovel_id | BIGINT | Sim |
| criado_em | DATETIME2(6) | Sim |

Restrições:

```sql
CONSTRAINT FK_interesses_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE
CONSTRAINT FK_interesses_imovel_id FOREIGN KEY (imovel_id) REFERENCES dbo.imoveis(id)
CONSTRAINT UQ_interesses_lead_imovel UNIQUE(lead_id,imovel_id)
```

## atividades

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| lead_id | BIGINT | Sim |
| titulo | NVARCHAR(160) | Sim |
| tipo | NVARCHAR(30) | Sim |
| prazo | DATETIME2(6) | Sim |
| concluida_em | DATETIME2(6) | Não |
| observacoes | NVARCHAR(2000) | Não |

Restrições:

```sql
CONSTRAINT FK_atividades_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE
CONSTRAINT CK_atividades_tipo CHECK (tipo IN (N'LIGACAO',N'MENSAGEM',N'DOCUMENTACAO',N'OUTRO'))
```

## visitas

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| lead_id | BIGINT | Sim |
| imovel_id | BIGINT | Sim |
| agendada_em | DATETIME2(6) | Sim |
| situacao | NVARCHAR(30) | Sim |
| virtual | BIT | Sim |
| km | DECIMAL(10,2) | Sim |
| observacoes | NVARCHAR(2000) | Não |

Restrições:

```sql
CONSTRAINT FK_visitas_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE
CONSTRAINT FK_visitas_imovel_id FOREIGN KEY (imovel_id) REFERENCES dbo.imoveis(id)
CONSTRAINT FK_visitas_interesse FOREIGN KEY (lead_id,imovel_id) REFERENCES dbo.interesses(lead_id,imovel_id)
CONSTRAINT CK_visitas_situacao CHECK (situacao IN (N'AGENDADA',N'REALIZADA',N'CANCELADA'))
CONSTRAINT CK_visitas_km CHECK (km>=0)
```

## propostas

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| lead_id | BIGINT | Sim |
| imovel_id | BIGINT | Sim |
| valor | DECIMAL(19,2) | Sim |
| situacao | NVARCHAR(30) | Sim |
| criada_em | DATETIME2(6) | Sim |
| observacoes | NVARCHAR(2000) | Não |

Restrições:

```sql
CONSTRAINT FK_propostas_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE
CONSTRAINT FK_propostas_imovel_id FOREIGN KEY (imovel_id) REFERENCES dbo.imoveis(id)
CONSTRAINT FK_propostas_interesse FOREIGN KEY (lead_id,imovel_id) REFERENCES dbo.interesses(lead_id,imovel_id)
CONSTRAINT CK_propostas_valor CHECK (valor>=0)
CONSTRAINT CK_propostas_situacao CHECK (situacao IN (N'PENDENTE',N'ACEITA',N'RECUSADA'))
```

## ocorrencias

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| lead_id | BIGINT | Sim |
| categoria | NVARCHAR(40) | Sim |
| descricao | NVARCHAR(2000) | Sim |
| acao | NVARCHAR(2000) | Não |
| criada_em | DATETIME2(6) | Sim |

Restrições:

```sql
CONSTRAINT FK_ocorrencias_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE
CONSTRAINT CK_ocorrencias_categoria CHECK (categoria IN (N'ATRASO_RETORNO',N'INFORMACAO_INCOMPLETA',N'VISITA_CANCELADA',N'FALHA_DOCUMENTACAO',N'OUTRO'))
```

## historico_etapas

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| lead_id | BIGINT | Sim |
| etapa_anterior | NVARCHAR(30) | Não |
| etapa_nova | NVARCHAR(30) | Sim |
| alterada_em | DATETIME2(6) | Sim |
| autor_id | BIGINT | Sim |

Restrições:

```sql
CONSTRAINT FK_historico_etapas_lead_id FOREIGN KEY (lead_id) REFERENCES dbo.leads(id) ON DELETE CASCADE
CONSTRAINT FK_historico_etapas_autor_id FOREIGN KEY (autor_id) REFERENCES dbo.usuarios(id)
```

## tokens_recuperacao

| Campo | Tipo SQL Server | Obrigatório |
|---|---|---|
| id | BIGINT | Sim |
| usuario_id | BIGINT | Sim |
| token_hash | NVARCHAR(64) | Sim |
| expira_em | DATETIME2(6) | Sim |
| usado | BIT | Sim |

Restrições:

```sql
CONSTRAINT FK_tokens_recuperacao_usuario_id FOREIGN KEY (usuario_id) REFERENCES dbo.usuarios(id)
CONSTRAINT UQ_tokens_recuperacao_token_hash UNIQUE (token_hash)
```

## Integridade dos interesses

Visitas e propostas têm chave estrangeira composta para `interesses(lead_id,imovel_id)`. O vínculo precisa existir e não pode ser removido enquanto estiver em uso. O Java exclui os filhos antes de excluir um lead aberto.

## Validação

O DDL foi revisado; instalação, triggers e procedures ainda devem ser executados em uma instância real de SQL Server.
