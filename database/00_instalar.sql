-- Execute a partir da pasta raiz do projeto, onde estão pom.xml e database/.
-- SSMS: ative Consulta > Modo SQLCMD e informe caminhos absolutos em :r se necessário.
-- Terminal: sqlcmd -S localhost -E -b -i database/00_instalar.sql
-- Parada em erro impede que um THROW seja ignorado nos próximos lotes GO.
:On Error exit
:r database/01_schema.sql
:r database/02_functions.sql
:r database/03_views.sql
:r database/04_triggers.sql
:r database/05_procedures.sql
PRINT 'Estrutura instalada. Dados iniciais opcionais: 06_dados_iniciais.sql.';
GO
