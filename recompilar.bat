@echo off
setlocal
cd /d "%~dp0"
call mvn clean package
if errorlevel 1 goto falhou
copy /y "target\nexus-0.3.2.jar" "dist\nexus.jar" >nul
if errorlevel 1 goto falhou
echo Codigo compilado, testes Maven executados e dist\nexus.jar atualizado.
pause
exit /b 0
:falhou
echo A compilacao ou a copia falhou. Confira os logs acima.
pause
exit /b 1
