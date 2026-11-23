# Guia de leitura do código — Nexus 0.3

O Nexus é um CRM para acompanhar clientes, imóveis, atividades, visitas e propostas. O código usa Java 17 e uma organização MVC comum. Cada módulo tem seu controlador e seus formulários HTML. As regras ficam em serviços com nomes relacionados ao negócio.

## Comece por uma operação pequena

Instale o esquema SQL Server por `database/00_instalar.sql`, configure a conexão e execute `iniciar-sqlserver.bat` (Windows) ou `bash iniciar-sqlserver.sh` (Linux/macOS). Acesse `http://localhost:8080`. Se tiver instalado `06_dados_iniciais.sql`, entre com `ana@example.test` e a senha de teste `NexoDemo2026!`; caso contrário, entre com o administrador configurado. Cadastre um lead e acompanhe estes arquivos:

| Ordem | Arquivo a abrir | O que procurar |
|---|---|---|
| 1 | `src/main/resources/templates/leads/form.html` | Campos `nome`, `telefone`, `origem` e `orcamento` |
| 2 | `forms/LeadForm.java` | Dados do formulário e validações |
| 3 | `web/LeadController.java` | Método `salvar`, que recebe o POST |
| 4 | `services/LeadService.java` | Método `salvar`, que aplica as regras |
| 5 | `services/AcessoService.java` | Como identifica o usuário e verifica permissões |
| 6 | `repositories/LeadRepository.java` | Consultas e persistência |
| 7 | `entities/Lead.java` | Campos e referências gravados no banco |

Os caminhos Java da tabela são relativos a `src/main/java/br/com/nexoimob/`.

Quando a pessoa clica em Salvar, o navegador envia um POST. O Spring preenche um `LeadForm` com os campos enviados. `@Valid` verifica as anotações, e `BindingResult` contém os erros. Se houver erro de preenchimento, o controlador mostra o mesmo formulário. Se os campos forem válidos, ele chama `LeadService.salvar`. Se uma regra do serviço recusar o cadastro, o controlador mostra a mensagem no mesmo formulário e mantém os campos preenchidos.

O serviço decide quem pode ser responsável pelo lead. Para um corretor, usa o usuário autenticado; um campo alterado no navegador não permite atribuir o atendimento a outra pessoa. O serviço preenche a entidade, grava pelo repositório e retorna o lead. O controlador redireciona para a ficha. Esse redirecionamento evita reenviar o formulário ao atualizar a página.

## Responsabilidade de cada pasta

| Pasta | Responsabilidade | Exemplo |
|---|---|---|
| `entities` | Representar os dados persistidos | `Lead`, `Imovel`, `Interesse` |
| `forms` | Receber apenas os campos permitidos no formulário | `VisitaForm` |
| `repositories` | Consultar e gravar no banco | `findByLeadResponsavelIdOrderByPrazoAsc` |
| `services` | Aplicar permissões, regras e transações | `AgendaService.salvarVisita` |
| `web` | Receber requisições e preparar as telas | `VisitaController` |
| `config` | Configurar autenticação e inicialização | `SecurityConfig`, `BootstrapAdmin` |
| `templates` | Exibir HTML com Thymeleaf | `visitas/form.html` |
| `static` | CSS e JavaScript do navegador | `css/app.css`, `js/app.js` |
| `database` | Instalar os objetos T-SQL | `00_instalar.sql` |

O controlador não decide se uma proposta pode ser aceita. Essa regra pertence ao serviço. O HTML apresenta as ações permitidas, mas a verificação também acontece no servidor.

## Anotações que aparecem no projeto

| Anotação | Significado neste projeto |
|---|---|
| `@Controller` | Classe que recebe requisições e devolve telas |
| `@GetMapping` | Atende uma consulta, como abrir um formulário |
| `@PostMapping` | Atende uma alteração enviada pelo formulário |
| `@PathVariable` | Recebe o ID presente na URL |
| `@RequestParam` | Recebe um parâmetro, como `leadId` |
| `@ModelAttribute` | Liga os campos da requisição ao objeto do formulário |
| `@Valid` | Solicita a validação das anotações do formulário |
| `@NotBlank`, `@NotNull` | Exigem texto preenchido ou um valor informado |
| `@Pattern` | Confere se o telefone normalizado tem DDD e a quantidade de dígitos aceita |
| `@Digits` | Limita os dígitos inteiros e as casas decimais antes de gravar |
| `@Service` | Registra uma classe de regras de negócio no Spring |
| `@Transactional` | Agrupa as alterações de uma operação em uma transação |
| `@Entity`, `@Table` | Mapeiam uma classe para uma tabela |
| `@ManyToOne`, `@JoinColumn` | Mapeiam uma referência para outra entidade |

Os serviços e repositórios chegam ao controlador pelo construtor. O Spring cria esses objetos e fornece as dependências. Os campos `private final` indicam que a referência recebida não será trocada depois da construção.

Os getters permitem ler campos privados. Os setters alteram os campos. Eles estão escritos explicitamente: não é necessário conhecer Lombok ou geração de código para acompanhar uma entidade.

## Relações entre os dados

Um cliente pode se interessar por vários imóveis, e um imóvel pode interessar a vários clientes. A tabela `interesses` representa esse N:N. A classe `Interesse` tem duas referências `@ManyToOne`: uma para `Lead` e outra para `Imovel`. O par é único.

O mapeamento é unidirecional: `Lead` e `Imovel` não possuem coleções `@OneToMany`. Os interesses são consultados pelo repositório. Visitas e propostas só podem usar um par já cadastrado nos interesses. No SQL Server, uma chave estrangeira composta também protege esse vínculo.

Os enums guardam os códigos, como `PENDENTE`, e possuem uma descrição para a tela, como “Pendente”. `BigDecimal` representa valores monetários. `LocalDateTime` representa os horários da aplicação.

## Regras que merecem revisão

| Regra | Onde ler |
|---|---|
| Corretor só altera seu atendimento | `AcessoService.verificarLead` |
| Usuário desativado ou com perfil alterado precisa entrar novamente | `VerificarSessaoFilter` |
| Alterar o e-mail não muda a identidade da sessão | `UsuarioLoginService`: autenticação guarda o ID |
| Lead encerrado preserva seus registros de atendimento | `LeadService.exigirAberto` e buscas para edição nos serviços |
| Visita virtual registra zero km | `AgendaService.salvarVisita` |
| Uma proposta aceita fecha lead e imóvel | `NegociacaoService.aceitar` |
| Cadastro público cria corretor inativo | `UsuarioService.cadastrar` |
| Senhas respeitam o limite de bytes do BCrypt | `SenhaUtil.validar` |
| Exportação contém somente atendimentos autorizados | `RelatorioService.exportarCSV` |

Na edição de atividade, visita ou proposta, o serviço verifica primeiro o registro original. Em seguida, verifica o cliente recebido pelo formulário. Assim, mudar `leadId` no POST não permite retirar um registro de um atendimento encerrado ou de outro corretor.

Ocorrências de qualidade podem ser registradas depois do encerramento para documentar ações corretivas. Essa regra é diferente das atividades de negociação e está indicada no serviço.

## Aceite de proposta e transação

Leia `NegociacaoService.aceitar`. O serviço chama `sp_aceitar_proposta` com parâmetros; a procedure no SQL Server bloqueia e confere os registros, aceita a proposta, marca o imóvel negociado e registra o ganho na mesma transação. Se uma regra falhar, a operação não deve deixar ganho incompleto. As procedures `sp_mover_lead` e `sp_agendar_visita` seguem a mesma ideia. O Java usa `?` para os valores e não concatena entradas do formulário. A apresentação acadêmica exige executar a solução em uma instância SQL Server real.

## Como revisar uma mudança

1. Identifique qual formulário e controlador recebem a alteração.
2. Confira o objeto em `forms`: só devem existir os campos que a pessoa pode informar.
3. Leia o serviço e suas permissões antes do `save`.
4. Confira as referências e restrições do banco.
5. Execute `mvn test` para as regras unitárias e reproduza o fluxo com o SQL Server configurado.
6. Se mudar uma regra T-SQL, atualize o script e execute também a verificação no SQL Server.

Os testes unitários atuais não exigem conexão ao banco. Os testes integrados da interface e dos objetos T-SQL precisam de uma instância SQL Server configurada; execute os scripts `08_verificacao.sql` e `09_teste_fluxo.sql` e repita os fluxos da interface.

Os formulários pequenos de acesso usam `@RequestParam`: o controlador recebe nome, email e senha, chama o serviço dentro de `try` e trata a recusa no `catch`. O `Model` conserva somente os campos de texto; a senha nunca é adicionada à resposta. No painel, um `if` inclui uma atividade somente quando ainda não foi concluída e seu atendimento está aberto. Nenhum registro é apagado por esse filtro.

## Exercícios para entender antes de apresentar

- Explique por que um corretor não consegue alterar um lead de outra pessoa, mesmo conhecendo seu ID.
- Mostre onde a etapa GANHO depende de uma proposta aceita.
- Acompanhe uma visita do HTML até o repositório e depois localize sua procedure.
- Explique por que remover um interesse usado por uma proposta é recusado.
- Modifique o título de uma atividade e confira o resultado pela aplicação.
- Explique como comprovar a conexão real com SQL Server e validar as procedures e os triggers.

Para uma primeira revisão, siga o cadastro de lead e depois o agendamento de visita. A configuração de segurança e a criação do administrador por `BootstrapAdmin` podem ser lidas depois que o fluxo principal estiver claro.


## Como explicar um cadastro na apresentação

O cadastro de lead é um exemplo completo de CRUD. Abra o método salvar do controlador e acompanhe o serviço, sem começar pela configuração de segurança.

1. O formulário envia os campos por POST.
2. O Spring preenche LeadForm. O setter de telefone remove espaços, parênteses, pontos e hífens; @Pattern confere o número resultante. @Digits recusa orçamento com mais de duas casas decimais.
3. O controlador usa BindingResult para decidir se deve mostrar erros ou continuar.
4. O serviço usa um if: sem ID cria uma entidade; com ID busca o registro existente. Depois verifica responsável e permissões.
5. Os setters copiam os campos permitidos para a entidade. O repositório grava com save.
6. O controlador redireciona para a ficha. Se o serviço lançar IllegalArgumentException por uma regra de negócio, o catch mostra a mensagem no formulário, sem apagar os campos.

Uma explicação possível: “O controlador recebe o formulário. A validação confere os campos. O serviço decide quem pode gravar e aplica as regras. O repositório salva a entidade no banco.” Depois mostre os trechos correspondentes.

## Conceitos que aparecem além do CRUD

| Conceito | Explicação neste projeto |
|---|---|
| Optional | Uma busca pode encontrar ou não um registro. isEmpty verifica a ausência; get lê o objeto quando ele existe. orElseThrow, usado em buscas simples, gera erro quando não encontra. |
| Construtor com dependências | O Spring fornece serviço e repositório prontos. Não criamos um repositório com new. |
| @Transactional | As alterações de uma operação ficam na mesma transação. Um erro desfaz a operação; isso evita fechar só parte da negociação. |
| @Lock | Impede o consumo simultâneo do mesmo token de recuperação. No aceite de proposta, os bloqueios estão na procedure SQL Server. |
| CommandLineRunner | O Spring executa run na inicialização. BootstrapAdmin cria o primeiro administrador quando a tabela de usuários está vazia, utilizando variáveis de ambiente. |
| @MappedSuperclass | BaseEntity reúne o ID que todas as entidades usam, sem criar outra tabela. |
| JdbcTemplate | Executa chamadas e consultas SQL com parâmetros. É necessário para usar os objetos SQL exigidos no trabalho. |

## Onde ficaram as lambdas

Os serviços de negócio e controladores não usam streams, referências de método nem lambdas. Há quatro expressões com -> em SecurityConfig porque a API de configuração do Spring Security recebe ações de configuração. Elas definem quais páginas são públicas, login, logout e acesso negado.

Por exemplo, em login -> login.loginPage("/login"), login é o configurador fornecido pelo Spring, e loginPage define a página de entrada. A busca do usuário está em UsuarioLoginService.loadUserByUsername, com Optional e if explícitos. As regras do CRM ficam fora dessas expressões.

## Como ler os recursos de segurança

UsuarioLoginService encontra o usuário pelo e-mail e entrega ao Spring seu ID, hash de senha, perfil e estado ativo. O Spring compara a senha recebida usando BCrypt. AcessoService confere o responsável antes de permitir o acesso a um lead.

VerificarSessaoFilter confere se o acesso continua ativo e com o mesmo perfil. PasswordService cria um link temporário, guarda o hash do token e invalida os links pendentes do usuário após redefinir a senha. Esses recursos atendem login, permissões e recuperação; não precisam ser misturados ao exemplo inicial de CRUD.

## Preparação para a defesa

Primeiro domine o cadastro, a edição e a exclusão de um lead. Depois explique interesses, visita e proposta. Por último, mostre as chamadas de procedure, as permissões e as transações. Use os testes como exemplos de entradas que devem ser aceitas ou recusadas; execute os mesmos casos pela interface.


## Interface e painel de indicadores

O frontend continua usando as páginas Thymeleaf, CSS e JavaScript do projeto. Não há uma aplicação separada nem biblioteca de gráficos. As alterações visuais preservam os controladores, serviços, repositórios e scripts SQL Server.

| Arquivo | Responsabilidade |
|---|---|
| `templates/home.html` | Indicadores, lista de clientes por etapa e círculo da carteira |
| `templates/fragments.html` | Cabeçalho, navegação e botão de tema compartilhados |
| `static/css/app.css` | Cores, espaçamento e adaptação ao celular |
| `static/js/theme.js` | Define o tema antes de carregar o CSS |
| `static/js/app.js` | Troca o tema pelo botão e mantém as interações existentes |
| `static/js/dashboard.js` | Busca as quantidades de clientes nas etapas do funil |

O tema escuro é o padrão. No tema claro, o CSS muda as variáveis de cor. O botão altera o atributo `data-theme` do HTML e guarda a escolha no `localStorage` do navegador.

O gráfico de etapas consulta a página `/leads`, que já aplica as permissões do usuário. Um `for` lê e soma as quantidades. Outro `for` preenche o número e a largura de cada barra. Há mensagens para carregamento, erro e ausência de clientes.

O círculo usa SVG e os números que o controlador do painel já fornece. Com `pathLength="100"`, a porcentagem de negociações abertas define o trecho colorido. Quando não há clientes, a porcentagem é zero, evitando divisão por zero.

Na apresentação, mostre primeiro os indicadores e as etapas; depois abra a página do funil para comparar as quantidades. Em seguida, alterne o tema e recarregue a página para mostrar que a escolha permanece salva.

Após editar os arquivos do frontend, execute `mvn package`. Copie `target/nexus-0.3.2.jar` para `dist/nexus.jar`, substituindo o executável anterior. O atalho usa o arquivo de `dist`; a troca de arquivos em `src/main/resources` só aparece nele depois dessa atualização.
