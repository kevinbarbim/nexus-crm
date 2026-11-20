# Nexus 0.3.1 — versão acadêmica pública

CRM imobiliário em Java 17, Spring Boot e Microsoft SQL Server.

## Para testar sem compilar

1. Baixe `Nexus_Publico_Executavel.zip` nos Assets desta Release e extraia.
2. Abra `README.md` na pasta `nexus` e siga o guia Windows com HeidiSQL.
3. Configure o SQL Server e execute o instalador incluído; depois, abra `iniciar-sqlserver.bat`.
4. Em uma base nova com carga de demonstração, use `admin@example.test` e `NexoDemo2026!`.

O pacote já inclui `dist/nexus.jar` corrigido, fontes, scripts, relatório e
planilha. Java e SQL Server são necessários; Maven não é necessário para usar
esse executável. Os ZIPs de fontes automáticos do GitHub não contêm o JAR.

## E-mail opcional

Não há credenciais reais neste pacote. Login e cadastros funcionam sem SMTP.
Para recuperação por e-mail, copie o exemplo SMTP para `smtp.local.properties`
e preencha as credenciais da própria conta, conforme o README. Cadastre um
usuário ativo com e-mail real para receber a mensagem; `.test` é fictício.

O JAR é o mesmo da revisão que a equipe executou localmente com SQL Server e
recuperação de senha por Gmail. Esse relato de teste manual não substitui a
conferência na máquina de quem baixa. A pasta `verificacao` reproduz cinco
checagens de empacotamento e 34 verificações isoladas, com repositórios e SMTP
locais de teste. Consulte os guias para os testes reais de banco e e-mail.
