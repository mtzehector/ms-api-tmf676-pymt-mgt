@echo off
REM ====================================================
REM Script para ejecutar todos los análisis de calidad
REM ====================================================

echo.
echo ========================================
echo  ANALISIS DE CALIDAD DE CODIGO
echo ========================================
echo.

REM Colores para Windows (opcional)
set GREEN=[92m
set RED=[91m
set YELLOW=[93m
set NC=[0m

echo %YELLOW%[1/6] Limpiando proyecto...%NC%
call mvn clean
if errorlevel 1 (
    echo %RED%ERROR: Fallo al limpiar proyecto%NC%
    pause
    exit /b 1
)

echo.
echo %YELLOW%[2/6] Ejecutando tests con cobertura...%NC%
call mvn test
if errorlevel 1 (
    echo %RED%ERROR: Tests fallaron%NC%
    pause
    exit /b 1
)

echo.
echo %YELLOW%[3/6] Generando reporte de cobertura JaCoCo...%NC%
call mvn jacoco:report
if errorlevel 1 (
    echo %RED%ERROR: Fallo al generar reporte de cobertura%NC%
)

echo.
echo %YELLOW%[4/6] Ejecutando SpotBugs...%NC%
call mvn spotbugs:check
if errorlevel 1 (
    echo %RED%WARNING: SpotBugs encontró issues%NC%
)

echo.
echo %YELLOW%[5/6] Ejecutando CheckStyle...%NC%
call mvn checkstyle:check
if errorlevel 1 (
    echo %RED%WARNING: CheckStyle encontró violaciones%NC%
)

echo.
echo %YELLOW%[6/6] Ejecutando PMD...%NC%
call mvn pmd:check
if errorlevel 1 (
    echo %RED%WARNING: PMD encontró issues%NC%
)

echo.
echo %GREEN%========================================%NC%
echo %GREEN% ANALISIS COMPLETADO%NC%
echo %GREEN%========================================%NC%
echo.
echo Reportes generados:
echo   - Cobertura JaCoCo: target\site\jacoco\index.html
echo   - SpotBugs: target\spotbugsXml.xml
echo   - CheckStyle: target\checkstyle-result.xml
echo   - PMD: target\pmd.xml
echo.

REM Preguntar si quiere abrir los reportes
set /p OPEN="Deseas abrir el reporte de cobertura? (S/N): "
if /i "%OPEN%"=="S" (
    start target\site\jacoco\index.html
)

pause
