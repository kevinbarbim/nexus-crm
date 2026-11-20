# Publicar o Nexus pelo site do GitHub

Este pacote separa os fontes em três lotes porque o navegador permite até
100 arquivos por envio. O aplicativo e a configuração manual de SMTP são os
mesmos da distribuição pública, sem credenciais reais.

## 1. Criar o repositório

1. Entre em https://github.com/new.
2. Nome: **nexus-crm**.
3. Descrição: **CRM imobiliário acadêmico em Java, Spring Boot e SQL Server — Fatec São José do Rio Preto.**
4. Escolha **Public**. Deixe **Add README** desativado, **.gitignore = None** e **License = None** por enquanto; os arquivos do projeto já estão preparados.
5. Clique em **Create repository**.

## 2. Enviar os três lotes

Extraia `Nexus_GitHub_Web.zip`. Dentro dele, há este guia e três pastas.
O guia solto serve para orientar o envio; os fontes estão nas pastas dos lotes.

| Ordem | Pasta local | Arquivos |
|---|---|---|
| 1 | `01_Projeto_e_documentacao` | 97 |
| 2 | `02_Codigo_Java` | 69 |
| 3 | `03_Interface_e_configuracao` | 33 |

Para cada lote:

1. No GitHub, permaneça na página principal do repositório, na aba **Code**.
2. No repositório vazio, clique em **uploading an existing file**. Nos próximos envios, escolha **Add file → Upload files**.
3. No Explorador de Arquivos do Windows, ENTRE na pasta do lote. Selecione seu conteúdo com **Ctrl+A** e arraste para a área de envio do GitHub.
4. Arraste o CONTEÚDO, não a pasta `01_...`, `02_...` ou `03_...`. Os caminhos exibidos no GitHub devem começar por `README.md`, `docs/`, `src/` etc., conforme o lote.
5. Aguarde o envio de todos os arquivos. Escreva uma mensagem de commit: **Adiciona projeto e documentação**, **Adiciona código Java** ou **Adiciona interface e configuração**.
6. Escolha gravar diretamente na branch principal (`main`) e confirme em **Commit changes**, conforme o botão exibido.
7. Volte à raiz do repositório antes de enviar o lote seguinte.

O segundo e o terceiro lote têm uma pasta chamada `src`. O envio do terceiro
acrescenta os recursos nessa mesma estrutura; não apaga os arquivos Java do
segundo. Não crie uma pasta extra chamada `nexus` no repositório.

Ao terminar, confira na raiz: `README.md`, `pom.xml`, `src`, `database`, `docs`
e `.gitignore`. O README deve aparecer formatado, com a imagem do projeto.
Os lotes são apenas uma organização para envio e não devem aparecer na raiz
do repositório publicado.

## 3. Disponibilizar o executável

1. Abra **Releases → Create a new release** ou **Draft a new release**.
2. Crie a tag **v0.3.1**, com destino à branch principal.
3. Título: **Nexus 0.3.1 — versão acadêmica**.
4. Use a descrição de `docs/Notas_Release.md`, disponível no repositório.
5. Anexe **Nexus_Publico_Executavel.zip**, sem extrair, na área de arquivos da Release e clique em **Publish release**.

O executável é distribuído na Release. O ZIP `Nexus_GitHub_Web.zip` serve
somente para organizar o envio dos fontes; extraia-o antes de enviar esses
fontes. O arquivo `Nexus_Publico_Executavel.zip` é o download pronto para quem
quer executar sem compilar.

## 4. Acabamento

Em **About**, confira a descrição e adicione os tópicos `java`, `spring-boot`,
`sql-server`, `thymeleaf`, `crm` e `fatec`. Confira os links, as imagens e o
resultado do workflow na aba Actions após concluir todos os lotes. A pasta
`.github` está no último lote, para que a verificação comece depois que o
projeto estiver completo.

Use apenas estes fontes públicos. Eles não contêm `smtp.local.properties`,
senha do banco ou senha de app do Gmail. O README explica a configuração
manual de e-mail; sua instalação pessoal existente permanece separada.

## Referências

- https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-new-repository
- https://docs.github.com/en/repositories/working-with-files/managing-files/adding-a-file-to-a-repository
- https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository
