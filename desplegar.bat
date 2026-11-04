@echo off
setlocal EnableExtensions DisableDelayedExpansion

if /I "%~1"=="--ayuda" goto :ayuda
if /I "%~1"=="/h" goto :ayuda

set "MODO=%~1"
if not defined MODO set "MODO=postgres"
if /I "%MODO%"=="postgres" goto :preparar
if /I "%MODO%"=="demo" goto :preparar
if /I "%MODO%"=="compilar" goto :preparar
echo [ERROR] Opcion desconocida. Usa: desplegar.bat [postgres^|demo^|compilar]
exit /b 1

:preparar
if /I "%MODO%"=="postgres" set "MODO=postgres"
if /I "%MODO%"=="demo" set "MODO=demo"
if not "%~2"=="" (
    echo [ERROR] Solo se admite una opcion. Usa desplegar.bat --ayuda
    exit /b 1
)
pushd "%~dp0"
if errorlevel 1 exit /b 1
set "RESULTADO=1"

if not exist "pom.xml" (
    echo [ERROR] No se encontro pom.xml junto a este archivo.
    goto :fin
)

if not defined JAVA_HOME goto :java_en_path
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if not exist "%JAVA_EXE%" goto :error_java
if not exist "%JAVA_HOME%\bin\javac.exe" goto :error_java
goto :maven

:java_en_path
where java.exe >nul 2>&1
if errorlevel 1 goto :error_java
where javac.exe >nul 2>&1
if errorlevel 1 goto :error_java
set "JAVA_EXE=java.exe"

:maven
set "MAVEN_CMD=%CD%\mvnw.cmd"
if exist "%MAVEN_CMD%" goto :compilar
set "MAVEN_CMD="
for /f "delims=" %%M in ('where mvn.cmd 2^>nul') do if not defined MAVEN_CMD set "MAVEN_CMD=%%M"
if defined MAVEN_CMD goto :compilar
echo [ERROR] No se encontro Maven. Instala Maven y agrega su carpeta bin al PATH.
goto :fin

:compilar
echo Compilando el backend y ejecutando las pruebas...
call "%MAVEN_CMD%" -B -ntp package
set "RESULTADO=%ERRORLEVEL%"
if not "%RESULTADO%"=="0" (
    echo [ERROR] Fallo la compilacion o alguna prueba. Revisa los mensajes anteriores.
    goto :fin
)

set "JAR=target\serf-1.0.0.jar"
if not exist "%JAR%" (
    echo [ERROR] No se genero el archivo %JAR%.
    set "RESULTADO=1"
    goto :fin
)
if /I "%MODO%"=="compilar" (
    echo Backend compilado: %JAR%
    goto :fin
)

echo.
echo Iniciando el backend con el perfil %MODO%...
echo Archivo de configuracion: "%CD%\.env"
echo Puerto HTTP: PORT en .env o en el entorno; 8080 por defecto.
echo Swagger disponible en /swagger-ui.html al terminar el arranque.
echo Registro del backend: logs\backend.log
echo Detener el backend: Ctrl+C. Mantiene esta ventana abierta mientras lo uses.
echo.
"%JAVA_EXE%" -jar "%JAR%" "--spring.profiles.active=%MODO%" "--logging.file.name=logs/backend.log"
set "RESULTADO=%ERRORLEVEL%"
if not "%RESULTADO%"=="0" echo [ERROR] El backend no pudo mantenerse en ejecucion. Revisa logs\backend.log.
goto :fin

:error_java
echo [ERROR] Se necesita un JDK 17 o superior. Revisa JAVA_HOME o el PATH.

:fin
popd
if not "%RESULTADO%"=="0" if "%~1"=="" pause
endlocal & exit /b %RESULTADO%

:ayuda
echo Uso: desplegar.bat [postgres^|demo^|compilar]
echo.
echo   postgres  Compila, prueba e inicia con PostgreSQL. Opcion predeterminada.
echo   demo      Compila, prueba e inicia con H2 en memoria. Datos temporales.
echo   compilar  Ejecuta las pruebas y genera target\serf-1.0.0.jar.
echo.
echo Requisitos: JDK 17 o superior y Maven en el PATH, o mvnw.cmd en el proyecto.
echo PostgreSQL lee DB_HOST, DB_PORT, DB_NAME, DB_USERNAME y DB_PASSWORD desde .env.
echo DB_URL permite reemplazar la conexion completa. Puerto HTTP: PORT en .env.
echo Las variables del sistema tienen prioridad sobre .env. Puerto por defecto: 8080.
exit /b 0
