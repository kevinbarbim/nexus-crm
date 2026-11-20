@echo off
setlocal DisableDelayedExpansion
cd /d "%~dp0"
title Nexus - SQL Server
if exist "configurar-sqlserver.local.bat" call "configurar-sqlserver.local.bat"
if not defined DB_URL set "DB_URL=jdbc:sqlserver://localhost:1433;databaseName=NexoImob;encrypt=true;trustServerCertificate=true"
if not defined DB_USER set "DB_USER=sa"
if not defined ADMIN_EMAIL set "ADMIN_EMAIL=admin@example.test"
if not defined ADMIN_PASSWORD set "ADMIN_PASSWORD=NexoDemo2026!"
where java >nul 2>&1
if errorlevel 1 goto semjava
if not exist "dist\nexus.jar" goto semjar
if defined DB_PASSWORD goto executar
echo Informe a senha do usuario SQL Server configurado em DB_USER.
echo Esta e a senha do banco, nao a senha do Gmail. Ela vale apenas nesta janela.
set /p "DB_PASSWORD=Senha do SQL Server: "
if not defined DB_PASSWORD goto semsenha
:executar
echo Iniciando Nexus em http://localhost:8080
echo O banco precisa estar instalado pelos scripts de database.
java -Duser.timezone=America/Sao_Paulo -jar "dist\nexus.jar" --server.address=127.0.0.1
if errorlevel 1 echo Confira o SQL Server, a porta, as credenciais e os logs acima.
pause
exit /b
:semsenha
echo Nenhuma senha de banco informada. Configure a conexao e tente novamente.
pause
exit /b 1
:semjava
echo Instale Java 17 ou superior compativel e tente novamente.
pause
exit /b 1
:semjar
echo Executavel ausente. Execute recompilar.bat em uma maquina com JDK 17 e Maven.
pause
exit /b 1
