# Checklist do projeto de extensão — 0.3.2

| Exigência | Implementação/evidência | Situação |
|---|---|---|
| Identificação da equipe | Cinco integrantes identificados na capa, na folha de rosto e no Quadro 2; cinco docentes na folha de rosto | Integrantes e docentes identificados |
| Carga integrada de 106 h | Planejamento no relatório: 40 + 20 + 24 + 22 | Planejado; não é apontamento de horas realizadas |
| Missão, visão, valores e comunicação | Síntese do site do Grupo NexGen e comunicação proposta para o piloto | Em relatório; avaliação operacional do uso é etapa futura |
| PESTEL, SWOT, Ishikawa e Pareto | Análises de processo e gráficos dos atendimentos | Preservado na versão 0.3 |
| Indicadores, fluxograma e melhoria | Atendimento, PDCA e visita virtual | Preservado na versão 0.3 |
| Java web com banco | Spring Boot, JPA, driver SQL Server | SQL Server exclusivo; execução local confirmada pela equipe |
| Cinco ou mais tabelas | 10 tabelas | Implementado |
| Relacionamento N:N | Leads × imóveis por interesses | Implementado |
| Dois perfis, login/logout e permissões | ADMIN e CORRETOR, controles de requisição | Implementado; repetir a demonstração dos dois perfis na apresentação |
| CRUD completo dos dados pertinentes | Oito cadastros operacionais; usuários administrados | Implementado com restrições de integridade |
| Perfil, cadastro e recuperação de senha | Cadastro sujeito à aprovação; token 15 min, uso único | Verificado isoladamente e recuperação local confirmada pela equipe; pacote público sem credenciais |
| Classes entities anotadas | Pacote entities e relações ManyToOne; associação N:N por Interesse | Implementado |
| Telas do sistema | Capturas reais da interface no relatório; 17 rotas em duas larguras conferidas | Capturas preservadas no relatório final |
| GitHub público com frontend/backend e README | Código completo e README no pacote | [Repositório público publicado](https://github.com/kevinbarbim/nexus-crm) |
| Vídeo com frontend, dados persistidos e permissões | Roteiro em docs | [Vídeo publicado](https://youtu.be/Iw9LiDzFyyQ) |
| DER e dicionário completo | DER único com dez entidades em SVG, PNG e PDF; modelo lógico DER.mmd; dicionário MD/JSON e Apêndice A | Preparados e conferidos com o esquema SQL |
| Inserções iniciais | 06_dados_iniciais.sql, base de 60 atendimentos | Carga fornecida; execução local informada pela equipe |
| Três procedures chamadas pelo Java | mover lead, agendar visita, aceitar proposta | Implementado; scripts 08/09 disponíveis para conferir os objetos e fluxos no ambiente de apresentação |
| Três consultas complexas via views | funil, Pareto e ambiental | Implementado; scripts 08/09 disponíveis para conferir os objetos e fluxos no ambiente de apresentação |
| Três triggers a partir de transações Java | histórico, propostas e visitas | Implementado; scripts 08/09 disponíveis para conferir os objetos e fluxos no ambiente de apresentação |
| Três funções de tipos diferentes utilizadas | escalar, inline TABLE, variável TABLE | Implementado; scripts 08/09 disponíveis para conferir os objetos e fluxos no ambiente de apresentação |
| Backup/restore (bônus) | 07_backup_restore.sql | Execução registrada no relatório pela equipe; manter os comprovantes do teste |
| Excel: linhas, barras e setores | Gráficos nativos, fórmulas recalculáveis | Conferido |
| Frequência, histograma, posição e dispersão | Abas Estatistica e Frequencias | Conferido |
| Regressão, dispersão, r e R² | Aba Regressao, 30 pares completos; vazio/texto excluído e zero visitas válido | Conferido |
| Benefícios/dificuldades/contribuição | Conclusão com avaliação recebida, benefícios esperados e dificuldades técnicas | Relatório atualizado |
| Apresentação e avaliação da empresa | Nexus apresentado ao Grupo NexGen; elogios ao design, painel, funil e ficha do cliente | Retorno positivo recebido e registrado |
| Diagnóstico organizacional | Felipe Brito, proprietário; datas de referência; sete processos e cinco problemas | Documentado no relatório e no Apêndice B |
| Documentos de apoio do diagnóstico | Fluxograma, seis capturas da interface, roteiro e quadro de benefícios | Apêndices C a F no relatório; arquivos de apoio em docs/anexos |
| Acompanhamento do uso pela organização | Piloto, linha de base e resultados operacionais | Avaliação futura; benefícios ainda não medidos |

A preparação da 0.3.2 repete cinco verificações de empacotamento e 34
verificações isoladas com substitutos de repositórios e SMTP local. O resultado
está em `verificacao/resultado_0_3_2.txt`. Esses testes não executam o SQL Server
nem o Gmail. Os relatos de execução local e de backup/restore pertencem à equipe,
conforme o relatório final; os scripts 08/09 e `Conferencia_Entrega.md` organizam
a conferência no computador de apresentação. A existência do vídeo não demonstra,
por si só, que cada item deste checklist foi testado.
