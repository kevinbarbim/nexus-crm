# Como explicar o dashboard e os temas

O backend continua com o CRUD em Spring Boot. Esta alteração acontece nos templates, no CSS e no JavaScript do navegador.

## Tema escuro e claro

1. `theme.js` lê a preferência salva em `localStorage`. Sem preferência, escolhe `dark`.
2. O JavaScript coloca `data-theme="dark"` ou `data-theme="light"` no elemento HTML.
3. `app.css` define as cores padrão em `:root`. A regra `[data-theme="light"]` troca essas cores.
4. O botão em `app.js` altera o atributo e salva a escolha. O `try/catch` permite continuar usando o botão quando o navegador bloqueia o armazenamento.

Não muda formulário, requisição do CRUD ou banco de dados. Todos os componentes usam as mesmas variáveis de cor.

## Barras dos clientes por etapa

O controlador do funil já calcula quantos clientes existem em cada etapa. O template `leads.html` escreve esse número em `data-count`.

A função `carregarEtapas()` em `dashboard.js` faz uma consulta GET para `/leads`. `DOMParser` transforma o HTML recebido em um documento, para ler seus sete atributos de contagem.

O primeiro `for` soma os clientes e encontra a maior quantidade. O segundo `for` escreve os números no dashboard e calcula a largura das barras:

```text
largura em porcentagem = 100 × quantidade da etapa ÷ maior quantidade
```

Quando todas as contagens são zero, a largura fica zero, evitando divisão por zero. O `catch` mostra a mensagem de erro; o `finally` encerra o estado de carregamento. As contagens vêm da tela autorizada para o usuário conectado.

## Círculo da carteira

O painel já recebe os valores de `totalLeads` e `ativos`. A quantidade encerrada é a diferença entre eles.

O SVG possui dois círculos: um fundo e um trecho colorido. Como `pathLength` vale 100, o atributo `stroke-dasharray` pode receber diretamente a porcentagem de clientes ativos. Os números e a legenda continuam como textos HTML.

## Onde alterar

- Cores, tamanhos e espaçamento: `src/main/resources/static/css/app.css`.
- Conteúdo do painel: `src/main/resources/templates/home.html`.
- Botão e persistência do tema: `static/js/theme.js` e `static/js/app.js`.
- Leitura das contagens: `static/js/dashboard.js`.

Os iniciadores executam `dist/nexus.jar`. Depois de editar o código-fonte, use `mvn package` para atualizar esse JAR antes de testar pelos iniciadores.
