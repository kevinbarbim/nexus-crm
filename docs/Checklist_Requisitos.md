# Checklist do projeto de extensão

| Exigência | Implementação/evidência | Situação |
|---|---|---|
| Identificação da equipe | Cinco integrantes identificados na capa, na folha de rosto e no Quadro 2; cinco docentes na folha de rosto | Integrantes e docentes identificados |
| Carga integrada de 106 h | Planejamento no relatório: 40 + 20 + 24 + 22 | Planejado; não é apontamento de horas realizadas |
| Missão, visão, valores e comunicação | Síntese do site do Grupo NexGen e comunicação proposta para o piloto | Em relatório; validação externa pendente |
| PESTEL, SWOT, Ishikawa e Pareto | Análises de processo e gráficos dos atendimentos | Preservado na versão 0.3 |
| Indicadores, fluxograma e melhoria | Atendimento, PDCA e visita virtual | Preservado na versão 0.3 |
| Java web com banco | Spring Boot, JPA, driver SQL Server | Código ajustado para somente SQL Server; conexão real pendente |
| Cinco ou mais tabelas | 10 tabelas | Implementado |
| Relacionamento N:N | Leads × imóveis por interesses | Implementado |
| Dois perfis, login/logout e permissões | ADMIN e CORRETOR, controles de requisição | Validar com SQL Server real |
| CRUD completo dos dados pertinentes | Oito cadastros operacionais; usuários administrados | Implementado com restrições de integridade |
| Perfil, cadastro e recuperação de senha | Cadastro sujeito à aprovação; token 15 min, uso único | Verificado isoladamente; SMTP Gmail preenchido, entrega real a testar |
| Classes entities anotadas | Pacote entities e relações ManyToOne; associação N:N por Interesse | Implementado |
| Telas do sistema | Capturas reais da interface no relatório; 17 rotas em duas larguras conferidas | Relatório atualizado para 0.3 |
| GitHub público com frontend/backend e README | Código completo e README no pacote | Publicação e link pendentes |
| Vídeo com frontend, dados persistidos e permissões | Roteiro em docs | Gravação e link pendentes |
| DER e dicionário completo | DER único com dez entidades em SVG, PNG e PDF; modelo lógico DER.mmd; dicionário MD/JSON e Apêndice A | Preparados e conferidos com o esquema SQL |
| Inserções iniciais | 06_dados_iniciais.sql, base de 60 atendimentos | Preparado; execução SQL Server pendente |
| Três procedures chamadas pelo Java | mover lead, agendar visita, aceitar proposta | Codificado; teste SQL Server pendente |
| Três consultas complexas via views | funil, Pareto e ambiental | Codificado; teste SQL Server pendente |
| Três triggers a partir de transações Java | histórico, propostas e visitas | Codificado; teste SQL Server pendente |
| Três funções de tipos diferentes utilizadas | escalar, inline TABLE, variável TABLE | Codificado; teste SQL Server pendente |
| Backup/restore (bônus) | 07_backup_restore.sql | Execução/comprovação pendentes |
| Excel: linhas, barras e setores | Gráficos nativos, fórmulas recalculáveis | Conferido |
| Frequência, histograma, posição e dispersão | Abas Estatistica e Frequencias | Conferido |
| Regressão, dispersão, r e R² | Aba Regressao, 30 pares completos; vazio/texto excluído e zero visitas válido | Conferido |
| Benefícios/dificuldades/contribuição | Conclusão com avaliação recebida, benefícios esperados e dificuldades técnicas | Relatório atualizado |
| Apresentação e avaliação da empresa | Nexus apresentado ao Grupo NexGen; elogios ao design, painel, funil e ficha do cliente | Retorno positivo recebido e registrado |
| Diagnóstico organizacional | Felipe Brito, proprietário; datas de referência; sete processos e cinco problemas | Documentado no relatório e no Anexo A |
| Documentos de apoio do diagnóstico | Fluxograma, seis capturas da interface, roteiro e quadro de benefícios | Anexos B a E preparados |
| Acompanhamento do uso pela organização | Piloto, linha de base e resultados operacionais | Avaliação futura; benefícios ainda não medidos |

A revisão 0.3.1 acompanha a suíte unitária do código e verificações isoladas de cadastro, recuperação e tratamento de erro, descritas em Revisao_0_3_1.md. Referências antigas a 36 testes de integração e 32 verificações HTTP não são comprovantes de aprovação desta edição. A execução integrada no SQL Server, a entrega real pelo Gmail e os links do repositório/vídeo ainda precisam ser conferidos pela equipe no ambiente de apresentação.
