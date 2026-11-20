# Correções da versão 0.2

Esta versão reorganiza o código para facilitar a leitura e corrige os problemas identificados na revisão da versão 0.1.

| Ponto | Alteração |
|---|---|
| Controlador e serviço concentravam muitos módulos | Controladores e serviços separados por assunto, sem controlador CRUD genérico |
| Campos HTML dinâmicos dificultavam rastrear uma operação | Formulários próprios, com campos explícitos e validação |
| Sessões conservavam acessos antigos | Conferência do usuário ativo e perfil a cada requisição autenticada |
| E-mail editado quebrava a sessão | A sessão usa o ID estável do usuário |
| POST permitia transferir filhos de atendimento encerrado | Verificação do vínculo original antes de modificar o registro |
| Perfil e logout desapareciam no celular | Menu de conta visível na versão móvel |
| Seleção de imóvel oferecia opções sem vínculo | Lista limitada aos interesses do cliente e validada no servidor |
| Busca mantinha contagens anteriores | Contagem do funil acompanha os cartões filtrados |
| Exportação repetia consultas por lead | Agregação de visitas e ocorrências em consultas por conjunto |
| Senhas Unicode podiam gerar erro pouco claro | Validação única do limite de 72 bytes UTF-8 |
| Motivos livres prejudicavam o Pareto | Cinco categorias iguais no Java, SQL e Excel |
| Instalação SQL podia prosseguir após erro | Instalador SQLCMD com parada em erro |
| Interesse usado podia ser removido no banco | Chaves estrangeiras compostas em visitas e propostas |
| DELETE SQL removia proposta aceita | Trigger também verifica exclusão |
| Documentação descrevia `@OneToMany` inexistente | Descrição do mapeamento unidirecional real |
| Dispersão mostrava só 60 linhas | Gráfico e medidas usam capacidade de 500 leads |
| Datas fixas e classes estreitas na planilha | Períodos e limites calculados a partir da base |
| Regressão com X constante produzia divisão por zero | Resultado indefinido exibido como `n.a.` |
| Moda limitada a 10 visitas | Cálculo sobre toda a base, com repetições explícitas |

A validação executada está em `Validacao.md`. SQL Server, SMTP externo, backup/restore, parceiro, publicação no GitHub e vídeo continuam dependendo das respectivas configurações ou evidências externas. Não são apresentados como etapas concluídas.
