#!/bin/bash
# ====================================================
# Script para ejecutar todos los análisis de calidad
# ====================================================

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo ""
echo "========================================"
echo "  ANALISIS DE CALIDAD DE CODIGO"
echo "========================================"
echo ""

# 1. Limpiar proyecto
echo -e "${YELLOW}[1/6] Limpiando proyecto...${NC}"
mvn clean
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Fallo al limpiar proyecto${NC}"
    exit 1
fi

# 2. Tests con cobertura
echo ""
echo -e "${YELLOW}[2/6] Ejecutando tests con cobertura...${NC}"
mvn test
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Tests fallaron${NC}"
    exit 1
fi

# 3. Reporte JaCoCo
echo ""
echo -e "${YELLOW}[3/6] Generando reporte de cobertura JaCoCo...${NC}"
mvn jacoco:report

# 4. SpotBugs
echo ""
echo -e "${YELLOW}[4/6] Ejecutando SpotBugs...${NC}"
mvn spotbugs:check
if [ $? -ne 0 ]; then
    echo -e "${RED}WARNING: SpotBugs encontró issues${NC}"
fi

# 5. CheckStyle
echo ""
echo -e "${YELLOW}[5/6] Ejecutando CheckStyle...${NC}"
mvn checkstyle:check
if [ $? -ne 0 ]; then
    echo -e "${RED}WARNING: CheckStyle encontró violaciones${NC}"
fi

# 6. PMD
echo ""
echo -e "${YELLOW}[6/6] Ejecutando PMD...${NC}"
mvn pmd:check
if [ $? -ne 0 ]; then
    echo -e "${RED}WARNING: PMD encontró issues${NC}"
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN} ANALISIS COMPLETADO${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "Reportes generados:"
echo "  - Cobertura JaCoCo: target/site/jacoco/index.html"
echo "  - SpotBugs: target/spotbugsXml.xml"
echo "  - CheckStyle: target/checkstyle-result.xml"
echo "  - PMD: target/pmd.xml"
echo ""

# Abrir reporte de cobertura (en macOS/Linux con xdg-open o open)
if command -v xdg-open &> /dev/null; then
    read -p "Deseas abrir el reporte de cobertura? (s/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Ss]$ ]]; then
        xdg-open target/site/jacoco/index.html
    fi
elif command -v open &> /dev/null; then
    read -p "Deseas abrir el reporte de cobertura? (s/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Ss]$ ]]; then
        open target/site/jacoco/index.html
    fi
fi
