# Correção do executável distribuído — 20/11/2026

O JAR anterior continha classes e templates, mas omitia entradas de diretórios
necessárias à descoberta de componentes pelo Spring. O resultado era uma
tela padrão de login, aviso de templates ausentes e zero repositórios JPA.

A correção acrescenta as entradas de diretórios. Os conteúdos de todas as
classes, recursos e dependências do JAR anterior foram preservados byte a byte.
Nenhuma regra de negócio, credencial ou configuração SQL foi alterada.

## Aplicar em uma instalação existente

1. Encerre a janela Java atual, usando Ctrl+C.
2. Substitua `dist/nexus.jar` pelo executável corrigido.
3. Execute novamente `iniciar-sqlserver.bat` e informe a senha do banco.
4. Abra http://localhost:8080/login e confira a tela do Nexus.

Com a carga de demonstração instalada, o administrador usa
`admin@example.test` e a senha de demonstração `NexoDemo2026!`.
Uma senha já alterada no banco deve ser mantida.

## Verificação realizada

- O mesmo teste de descoberta falhou no JAR anterior e passou no corrigido.
- Cinco verificações pelo carregador do JAR passaram: diretório de templates,
  arquivo do login, descoberta das 68 classes, dos componentes essenciais e
  dos 10 repositórios.
- As 34 verificações isoladas existentes passaram, com três mensagens
  capturadas somente no SMTP local.
- Um teste HTTP carregou os componentes e templates do JAR corrigido com
  repositórios substitutos em memória: login, cadastro, recuperação, CSS,
  proteção da página de usuários, autenticação e página de usuários passaram.
- A inicialização normal passou a descobrir os 10 repositórios JPA e parou
  na conexão SQL propositalmente indisponível no ambiente de verificação.

Os testes com substitutos não comprovam persistência, procedimentos,
transações ou consultas no SQL Server. A entrega pelo Gmail não foi testada.
O projeto continua usando exclusivamente SQL Server em sua execução normal.

## Reproduzir a conferência do pacote

`python verificacao/verificar_revisao.py` requer Python 3.9+ e JDK 17+.
Resultados esperados: `TOTAL_EMPACOTAMENTO=5`, `TOTAL_PASS=34` e três mensagens
no SMTP local. O teste de empacotamento usa o carregador do próprio JAR;
essa etapa não se apoia nas classes extraídas para validar os diretórios.
