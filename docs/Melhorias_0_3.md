# Melhorias da versão 0.3

Esta revisão mantém os módulos do CRM e torna o backend mais direto para a leitura de quem estudou CRUD básico em Spring Boot. A organização continua HTML → formulário → controlador → serviço → repositório → entidade. As funções de login, autorização, recuperação de senha, transações e integração com SQL Server foram preservadas.

## O que ficou mais simples

| Trecho | Alteração | Como explicar |
|---|---|---|
| Inicialização do administrador | `BootstrapAdmin` implementa `CommandLineRunner` com `run` explícito | O Spring chama o método ao iniciar; ele cria o primeiro administrador se a tabela estiver vazia |
| Cadastros iniciais | `06_dados_iniciais.sql` executa inserções e relacionamentos diretamente no SQL Server | Os dados fictícios são opcionais; o primeiro administrador também pode ser criado por `BootstrapAdmin` |
| Busca do usuário no login | Classe `UsuarioLoginService` com `Optional` e `if` | Busca pelo e-mail; se não existir, recusa o login; se existir, entrega os dados ao Spring |
| Cadastro e edição | Criação e busca da entidade escritas com `if` | Sem ID cria; com ID consulta e altera o registro autorizado |
| Repositórios | Declarações antes comprimidas divididas em linhas | Interface, método de consulta e parâmetros ficam visíveis |

Os serviços de negócio, controladores e cargas iniciais não usam streams, referências de método nem lambdas. Quatro lambdas curtas continuam em `SecurityConfig`, porque são a forma atual de configurar a API do Spring Security usada no projeto. Elas configuram páginas públicas, login, logout e acesso negado. A busca do usuário e as regras do CRM ficam em métodos explícitos.

`@Transactional` e os bloqueios de aceite/token permanecem: evitam alterações parciais e duas operações usando o mesmo registro ao mesmo tempo. `JdbcTemplate` também permanece para chamar as procedures, views e funções exigidas. Retirar esses recursos mudaria o comportamento do sistema.

## Correções funcionais

| Problema | Resultado nesta versão |
|---|---|
| Telefone livre aceitava letras | Normaliza a formatação comum e exige DDD com telefone de 10 ou 11 dígitos; código 55 opcional |
| Mais casas decimais que o banco | `@Digits` limita valores, área e km à precisão do respectivo `DECIMAL`, antes de persistir |
| POST sem CSRF resultava em erro de método na tela de acesso negado | O encaminhamento aceita o POST e mantém a resposta HTTP 403 |
| Regra de negócio apagava o formulário preenchido | O controlador apresenta a mensagem no próprio formulário, preservando os campos; resposta HTTP 400 |
| Imóvel poderia ser confundido com empreendimento inteiro | Ajuda do formulário orienta cadastrar uma unidade negociável por registro |
| Visitas ausentes entravam como zero na regressão | X e Y entram juntos somente quando ambos são numéricos; zero visitas continua válido |
| Contagens na planilha sem validação | Visitas, tarefas atrasadas e ocorrências recebem validação de inteiro não negativo |

## Como conferir

Execute `mvn test`: os 28 testes passaram nesta revisão, incluindo os 20 anteriores. O JAR 0.3 foi conferido em 34 combinações de rota e largura, com login, permissões, CRUDs, CSRF, exportação e aceites simultâneos. Um erro de negócio foi reproduzido no navegador com campos preservados. A planilha foi conferida com visitas em branco, texto e zero, restaurando os registros originais antes de salvar.

Use `Guia_do_Codigo.md` para seguir um cadastro completo e preparar a explicação. `Validacao.md` distingue as verificações concluídas das pendências. O relatório recebeu versão e número de testes atualizados, títulos numerados e sumário com links internos. O roteiro de diagnóstico contém campos para registrar entrevista, indicadores, piloto e devolutiva reais.

SQL Server real, SMTP externo, evidências com a empresa, publicação e vídeo continuam pendentes. Os três objetos de cada categoria T-SQL e suas chamadas Java foram mantidos; a execução em SQL Server precisa ser conferida na instância de avaliação.
