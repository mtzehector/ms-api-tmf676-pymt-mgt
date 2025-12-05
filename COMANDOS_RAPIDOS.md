# ⚡ Comandos Rápidos - Referencia

## 🧪 Testing

```bash
# Tests básicos
mvn test

# Tests con cobertura
mvn clean test jacoco:report

# Test específico
mvn test -Dtest=PaymentControllerTest

# Tests de arquitectura
mvn test -Dtest=ArchitectureTest

# Ver reporte de cobertura
start target/site/jacoco/index.html   # Windows
open target/site/jacoco/index.html    # Mac
xdg-open target/site/jacoco/index.html # Linux

# Mutation testing (requiere plugin Pitest)
mvn pitest:mutationCoverage
start target/pit-reports/index.html
```

---

## 🔍 Análisis de Calidad

```bash
# Análisis completo (recomendado antes de commit)
mvn clean verify spotbugs:check checkstyle:check pmd:check

# O usar el script:
run-quality-checks.bat   # Windows
./run-quality-checks.sh  # Linux/Mac

# SpotBugs solo
mvn spotbugs:check
# Ver: target/spotbugsXml.xml

# CheckStyle solo
mvn checkstyle:check
# Ver: target/checkstyle-result.xml

# PMD solo
mvn pmd:check
# Ver: target/pmd.xml

# SonarCloud
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
# Ver: https://sonarcloud.io
```

---

## 🔒 Seguridad

```bash
# Análisis de vulnerabilidades (requiere plugin OWASP)
mvn dependency-check:check
start target/dependency-check-report.html

# Actualizar todas las dependencias
mvn versions:display-dependency-updates

# Verificar versiones de plugins
mvn versions:display-plugin-updates
```

---

## 🏗️ Build y Deploy

```bash
# Build completo
mvn clean install

# Build sin tests
mvn clean install -DskipTests

# Build y generar JAR
mvn clean package

# Ver el JAR generado
ls -l boot/target/*.jar

# Ejecutar aplicación
java -jar boot/target/management-boot-0.0.1.jar

# O con Maven
mvn spring-boot:run -pl boot
```

---

## 📊 Reportes

```bash
# Generar sitio con todos los reportes
mvn clean site

# Ver sitio generado
start target/site/index.html

# Reporte de dependencias
mvn project-info-reports:dependencies

# Árbol de dependencias
mvn dependency:tree

# Buscar conflictos
mvn dependency:analyze
```

---

## 🧹 Limpieza

```bash
# Limpiar todo
mvn clean

# Limpiar y eliminar directorios de IDE
mvn clean
rm -rf .idea target */target

# Limpiar caché de Maven
rm -rf ~/.m2/repository/mx/att/digital/api

# Forzar re-descarga de dependencias
mvn clean install -U
```

---

## 🐛 Debug y Troubleshooting

```bash
# Ejecutar con debug
mvn test -X

# Ver qué tests se ejecutan
mvn test -Dtest=* -DfailIfNoTests=false

# Ejecutar solo un método de test
mvn test -Dtest=PaymentControllerTest#createPayment_returnsAccepted

# Ver stack traces completos
mvn test -Dsurefire.printSummary=true

# Verificar compilación
mvn clean compile

# Verificar solo tests de compilación
mvn clean test-compile
```

---

## 🔧 IntelliJ IDEA

```bash
# Regenerar archivos de proyecto
mvn idea:idea

# Reimportar proyecto Maven
# En IntelliJ: Right-click pom.xml → Maven → Reload Project
```

---

## 🚀 Acciones Rápidas Pre-Commit

```bash
# Opción 1: Rápido (< 1 min)
mvn clean test

# Opción 2: Completo (2-3 min)
mvn clean test spotbugs:check checkstyle:check

# Opción 3: Full (5-10 min) - antes de PR
mvn clean verify sonar:sonar spotbugs:check checkstyle:check pmd:check

# Opción 4: Con script
run-quality-checks.bat
```

---

## 📦 Dependencias

```bash
# Ver todas las dependencias
mvn dependency:list

# Ver dependencias en árbol
mvn dependency:tree

# Buscar dependencia específica
mvn dependency:tree -Dincludes=org.springframework:spring-web

# Copiar dependencias a directorio
mvn dependency:copy-dependencies

# Analizar dependencias no usadas
mvn dependency:analyze

# Resolver conflictos de versiones
mvn dependency:tree -Dverbose
```

---

## 🎯 Perfiles Maven

```bash
# Ejecutar con perfil específico
mvn clean install -P tmforum

# Ver perfiles activos
mvn help:active-profiles

# Lista de todos los perfiles
mvn help:all-profiles
```

---

## 📈 Métricas y Análisis

```bash
# Líneas de código
find src -name "*.java" | xargs wc -l

# Número de tests
find src/test -name "*Test.java" | wc -l

# Complejidad ciclomática (requiere plugin PMD)
mvn pmd:pmd
grep "Cyclomatic Complexity" target/pmd.xml

# Código duplicado
mvn pmd:cpd
```

---

## 🔄 Git + Maven

```bash
# Verificar antes de commit
mvn clean test && git add . && git commit -m "mensaje"

# Pre-push hook
mvn clean verify && git push

# Limpiar archivos no rastreados
git clean -fdx
mvn clean install
```

---

## 💡 Tips y Trucos

```bash
# Ejecutar Maven en paralelo (más rápido)
mvn -T 4 clean install  # 4 threads

# Ejecutar solo módulo específico
mvn clean install -pl domain

# Ejecutar módulo y dependencias
mvn clean install -pl boot -am

# Saltar módulos
mvn clean install -pl !logging-core

# Continuar desde módulo específico (si falló antes)
mvn clean install -rf :management-api-rest

# Ver propiedades efectivas
mvn help:effective-pom

# Ver settings de Maven
mvn help:effective-settings

# Offline mode (más rápido si ya tienes dependencias)
mvn clean install -o
```

---

## 🎨 Formateo de Código

```bash
# IntelliJ IDEA shortcuts:
# Ctrl+Alt+L - Format code
# Ctrl+Alt+O - Optimize imports
# Ctrl+Shift+F9 - Recompile

# Maven formatter (si tienes plugin)
mvn formatter:format

# Verificar formato sin cambiar
mvn formatter:validate
```

---

## 🆘 Comandos de Emergencia

```bash
# Si IntelliJ no reconoce clases
mvn clean install
# Luego: File → Invalidate Caches → Invalidate and Restart

# Si tests fallan por memoria
mvn clean test -DargLine="-Xmx2048m"

# Si falla por timeout
mvn clean test -Dsurefire.timeout=600

# Si hay problemas con Lombok
mvn clean compile -Dlombok.version=1.18.38

# Forzar re-compilación completa
mvn clean install -U -DskipTests
```

---

## ⚙️ Variables de Entorno Útiles

```bash
# Ver versión de Java
java -version
echo $JAVA_HOME

# Ver versión de Maven
mvn -version

# Configurar memoria para Maven
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# Windows
set MAVEN_OPTS=-Xmx2048m -XX:MaxPermSize=512m
```

---

## 📝 Aliases Recomendados (Bash/Zsh)

```bash
# Agregar a ~/.bashrc o ~/.zshrc

alias mci="mvn clean install"
alias mct="mvn clean test"
alias mcv="mvn clean verify"
alias mcp="mvn clean package"
alias mst="mvn spotbugs:check"
alias mcs="mvn checkstyle:check"
alias mpmd="mvn pmd:check"
alias mqc="mvn clean test spotbugs:check checkstyle:check pmd:check"
alias mjacoco="mvn jacoco:report && open target/site/jacoco/index.html"
alias msonar="mvn clean verify sonar:sonar"
```

---

## 🎯 Workflow Recomendado

### Desarrollo Diario:
```bash
1. git pull
2. mvn clean test                    # Verificar que todo funciona
3. [hacer cambios]
4. mvn test                          # Tests rápidos
5. [revisar SonarQube IDE]
6. mvn clean test spotbugs:check     # Antes de commit
7. git add . && git commit -m "..."
8. git push
```

### Antes de Pull Request:
```bash
1. mvn clean verify
2. mvn spotbugs:check checkstyle:check pmd:check
3. mvn sonar:sonar
4. [revisar SonarCloud dashboard]
5. mvn pitest:mutationCoverage       # Opcional
6. [crear PR]
```

### Antes de Release:
```bash
1. mvn clean install
2. mvn dependency-check:check
3. mvn sonar:sonar
4. mvn site
5. [revisar todos los reportes]
6. git tag -a v1.0.0 -m "Release 1.0.0"
7. git push --tags
```
