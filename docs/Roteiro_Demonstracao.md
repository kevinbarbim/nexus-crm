# Roteiro de demonstração — 7 a 9 minutos

Apresente o Nexus como proposta de CRM para o Grupo NexGen, de São José do Rio Preto/SP. Use a base SQL Server com os cadastros iniciais e mostre um atendimento completo, do interesse à proposta. A apresentação final deve demonstrar SQL Server, inclusive a persistência após reiniciar o Java.

| Tempo | Demonstrar | Evidência |
|---|---|---|
| 0:00–0:50 | Login como administrador, painel e função de cada perfil | Navegação e regras de acesso |
| 0:50–1:50 | Criar lead e unidade imobiliária, consultar e editar; mostrar telefone inválido e um erro sem apagar o formulário | Validação na tela e SELECT em `leads` e `imoveis` no HeidiSQL |
| 1:50–2:30 | Vincular o lead a dois imóveis; mostrar um imóvel com dois leads | N:N em `interesses`, sem duplicar o par |
| 2:30–3:20 | Alterar etapa e agendar visita virtual com zero km | Procedure de etapa/visita e histórico automático |
| 3:20–4:10 | Cadastrar proposta e aceitar | Proposta ACEITA, imóvel NEGOCIADO, lead GANHO no mesmo fluxo |
| 4:10–5:00 | Relatórios com as três views e três funções | Tela e SELECTs do script 08 |
| 5:00–5:50 | Login como corretor e acesso a lead de outro corretor por URL | Seus registros visíveis e acesso indevido negado |
| 5:50–6:30 | Excluir registros de um atendimento de teste ainda aberto; mostrar bloqueio de registro em uso | CRUD e integridade referencial |
| 6:30–7:10 | Exportar CSV e recalcular a planilha | Gráficos, medidas, regressão, r e R² |
| 7:10–8:00 | Cadastro, aprovação, perfil e recuperação de senha | Cadastro sem escolha de ADMIN; SMTP configurado para recuperação por e-mail |
| 8:00–9:00 | Reiniciar a aplicação e mostrar os mesmos dados; backup/restore se executado | Persistência SQL Server e restauração em outra base |

Confira no HeidiSQL os objetos `P`, `V`, `TR`, `FN`, `IF` e `TF`. Para verificar os triggers, use operações pelo Java e consulte o histórico. Em um teste SQL controlado, confira também a rejeição de visita virtual com km > 0 e proposta sem interesse.

Antes da gravação: executar os scripts e os três fluxos reais no SQL Server; concluir o teste de restauração; configurar SMTP; publicar o código completo em um repositório GitHub público; gravar o vídeo sem senhas reais ou dados pessoais. Inserir os links reais no relatório. Esses passos ainda não foram executados nesta versão.

Para explicar o backend, acompanhe um cadastro do HTML até o repositório usando `Guia_do_Codigo.md`. Mostre `if`, validações e setters; depois explique a transação da proposta e localize suas chamadas SQL. A configuração de segurança pode ser explicada ao final, separada do fluxo principal de CRUD.

## Material de apoio ao estudo de caso

O roteiro de referência da apresentação à organização está em `anexos/Anexo_D_Apresentacao.md`. O estudo de caso adota 05/10/2026 como data de referência da apresentação. O roteiro deste arquivo continua destinado à gravação técnica final, com SQL Server e persistência; a gravação permanece pendente.
