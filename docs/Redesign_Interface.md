# Interface — Nexus

Atualização de 10/10/2026 sobre a versão acadêmica 0.3.

## Aparência

O tema padrão tem fundo preto, superfícies em cinza escuro, bordas discretas e texto claro. A fonte continua sendo Inter Variable 4.1, servida localmente. O menu compacto e a hierarquia visual seguem a referência do [Linear](https://linear.app/), mantendo a marca Nexus e a identificação do Grupo NexGen.

O botão no cabeçalho alterna entre tema claro e escuro. Nas telas públicas, ele fica junto da marca. A preferência fica salva no navegador e é aplicada antes do CSS, para evitar abrir uma página clara quando o usuário escolheu o escuro. Se o armazenamento estiver bloqueado, a troca continua funcionando na página atual.

Os mesmos componentes e regras de layout atendem aos dois temas. As cores ficam nas variáveis no início de `app.css`; não há dois arquivos de estilo independentes.

O login apresenta os campos de acesso. O painel reúne indicadores, distribuição por etapa e retornos. As contas de acesso e os perfis de execução estão descritos no README.

## Dashboard

| Componente | Origem dos valores | Implementação |
|---|---|---|
| Leads cadastrados | `totalLeads`, já calculado no painel | Texto Thymeleaf |
| Negociações abertas | `ativos`, já calculado no painel | Texto Thymeleaf |
| Conversão | `conversao`, ganhos entre atendimentos encerrados | Texto Thymeleaf |
| Atividades atrasadas | `atrasadas`, atividades pendentes com prazo vencido | Texto Thymeleaf |
| Clientes por etapa | Contagens já calculadas na tela `/leads` | Uma consulta GET, sete barras em HTML/CSS |
| Andamento da carteira | Abertos = `ativos`; encerrados = `totalLeads - ativos` | Dois círculos SVG e textos Thymeleaf |
| Atividades pendentes e últimos atendimentos | Listas existentes do controlador | Links e listas Thymeleaf |

O gráfico de etapas representa a distribuição atual, não uma evolução no tempo. A maior contagem ocupa toda a largura disponível; as demais barras são proporcionais a ela. A quantidade é escrita ao lado de cada barra.

O círculo usa `pathLength="100"`. Assim, a porcentagem de clientes abertos é aplicada diretamente em `stroke-dasharray`, sem trigonometria nem biblioteca de gráficos. A divisão só acontece quando a carteira tem clientes.

O gráfico de etapas tem mensagem de carregamento, tratamento de contagens vazias, erro e um link para tentar novamente. A consulta usa a sessão existente e respeita a carteira autorizada do corretor. Se a sessão expirar ou a resposta não contiver as contagens esperadas, o gráfico mostra erro em vez de apresentar números inventados.

## Arquivos e leitura do código

| Arquivo | Responsabilidade |
|---|---|
| `static/css/app.css` | Cores dos dois temas, componentes, gráficos e regras para celular |
| `static/js/theme.js` | Dez linhas para escolher o tema antes de desenhar a página |
| `static/js/app.js` | Botão de tema e comportamentos anteriores de menu, senha, diálogo e consulta de imóveis |
| `static/js/dashboard.js` | Script de 49 linhas que consulta e apresenta as contagens do funil |
| `templates/fragments.html` | Cabeçalhos, marca, botão de tema, menu e mensagens comuns |
| `templates/home.html` | Indicadores, barras, círculo e listas do dashboard |
| `templates/leads.html` | Contagens existentes expostas como atributos `data-count` |
| `templates/relatorios.html` | Remoção do aviso da tela |
| `templates/error.html` | Acesso ao botão de tema quando não há usuário conectado |

O front-end continua em Thymeleaf, CSS e JavaScript comum. Os novos trechos usam funções nomeadas, `if`, `for` e `try/catch`. Não foram acrescentados frameworks, bibliotecas de gráficos ou dependências Maven. Veja o [Guia da interface](Guia_Interface.md) para explicar a implementação na apresentação.

A fonte local é idêntica ao arquivo `InterVariable.woff2?v=4.1` servido pelo Linear e conferido na atualização anterior: 352.240 bytes, SHA-256 `693b77d4f32ee9b8bfc995589b5fad5e99adf2832738661f5402f9978429a8e3`. Sua licença acompanha o projeto.

## Verificação desta atualização

- 385 verificações em 172 visitas para os fluxos do CRM, registradas em `Validacao_Interface.json`.
- 678 verificações e 152 visitas de layout para os temas e o dashboard, registradas em `Validacao_Temas.json`.
- Telas em 320, 390, 768, 1024 e 1366 pixels, nos temas claro e escuro, sem rolagem horizontal da página. Tabelas e funil mantêm a rolagem interna.
- Preferência de tema após recarregar e navegar, padrão escuro mesmo com o sistema claro, troca pelo teclado e funcionamento com armazenamento bloqueado.
- Contagens do gráfico e proporção do círculo conferidas nas carteiras do administrador (60 clientes) e da corretora (30 clientes), preservando a autorização.
- Carregamento, erro, tentativa de recuperação e ausência de dados do gráfico conferidos com respostas simuladas; nenhum cadastro foi apagado para esses cenários.
- Contraste dos textos principais, rótulos, botões e mensagens nos dois temas: menor razão encontrada nos elementos amostrados = 4,88:1.
- Login e saída, pesquisa, validação, cadastro/edição de cliente, mudança de etapa, interesses, atividades, confirmação e cancelamento, consulta de imóveis, visita, proposta, aceite, exportação CSV e autorização conferidos.
- Nenhum erro de JavaScript nos cenários executados. A suíte dos fluxos não registrou HTTP 500. As falhas HTTP 503 do gráfico foram simuladas intencionalmente.

Os formulários operacionais dos 24 templates foram comparados com a versão anterior: métodos, destinos e campos preservados. Os arquivos de Java, testes, configuração, Maven, SQL e dados do projeto também foram comparados e permaneceram iguais. As 280 entradas do JAR fora de templates e arquivos estáticos foram preservadas byte a byte.

Esta seção relata uma verificação visual de edição anterior. O Java e o executável desta edição foram recompilados para uso exclusivo com SQL Server. A execução em uma instância SQL Server e o SMTP externo ainda exigem validação específica.

## Capturas

| Tela | Arquivo |
|---|---|
| Painel escuro | [painel.png](telas/painel.png) |
| Painel claro | [painel_claro.png](telas/painel_claro.png) |
| Celular, escuro | [mobile.png](telas/mobile.png) |
| Celular, claro | [mobile_claro.png](telas/mobile_claro.png) |
| Menu no celular | [menu_mobile.png](telas/menu_mobile.png) |
| Login escuro | [login.png](telas/login.png) |
| Login claro | [login_claro.png](telas/login_claro.png) |
| Login no celular | [login_mobile.png](telas/login_mobile.png) |
| Funil | [funil.png](telas/funil.png) |
| Ficha | [ficha_lead.png](telas/ficha_lead.png) |
| Cadastro de cliente | [cadastro_lead.png](telas/cadastro_lead.png) |
| Imóveis | [imoveis.png](telas/imoveis.png) |
| Relatórios | [relatorios.png](telas/relatorios.png) |
| Erro de formulário | [erro_formulario.png](telas/erro_formulario.png) |
| Confirmação | [confirmacao.png](telas/confirmacao.png) |

## Executar

Após instalar e configurar o SQL Server, execute `iniciar-sqlserver.bat` no Windows ou `bash iniciar-sqlserver.sh` no Linux/macOS. Abra http://localhost:8080. O JAR em `dist/` inclui o dashboard e os dois temas.

Para recompilar o Java, continuam valendo `mvn test` e `mvn package`. A instalação e a conexão SQL Server seguem o README e os scripts existentes.

## Logo Nexus

O componente `marca` de `fragments.html` usa o PNG original em `static/css/nexus-logo.png`. O mesmo arquivo é usado no ícone da aba. O símbolo mantém um fundo escuro nos dois temas, e `object-fit: contain` conserva suas proporções. A troca é centralizada nesse componente e aparece nas telas de acesso e no menu do CRM.
