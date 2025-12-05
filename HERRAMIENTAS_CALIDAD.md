# 🛠️ Herramientas de Calidad para el Proyecto

## 🔌 Plugins para IntelliJ IDEA

### 1. SonarQube for IDE ✅ (Ya instalado)
- **Instalación**: Settings → Plugins → Marketplace → "SonarQube for IDE"
- **Conexión a SonarCloud**:
  - Settings → Tools → SonarQube for IDE
  - Add SonarCloud connection
  - Token: Genera desde https://sonarcloud.io/account/security

### 2. SpotBugs
- **Instalación**: Settings → Plugins → "SpotBugs"
- **Uso**: Analiza → SpotBugs → Analyze Current File
- Detecta: Null pointer, resource leaks, security issues

### 3. CheckStyle-IDEA
- **Instalación**: Settings → Plugins → "CheckStyle-IDEA"
- **Configuración**: Settings → Tools → Checkstyle
- Usa: Google Java Style o configuración personalizada

### 4. PMDPlugin
- **Instalación**: Settings → Plugins → "PMDPlugin"
- **Uso**: Tools → Run PMD → Check Code with PMD
- Detecta: Code smells, complexity issues

### 5. Code Coverage (Built-in)
- **Uso**: Run → Run with Coverage
- **Ver**: Run panel → Coverage tab
- Identifica: Código no testeado

### 6. JaCoCo Integration
- Ya configurado en pom.xml
- **Ver reportes**: `target/site/jacoco/index.html` después de `mvn test`

### 7. Veracode Integration
- **Instalación**: Settings → Plugins → "Veracode"
- Requiere: Licencia de Veracode
- Análisis de seguridad en tiempo real

## 📦 Dependencias Maven para Testing

### 1. ArchUnit (Tests de Arquitectura)
```xml
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>1.3.0</version>
    <scope>test</scope>
</dependency>
```

**Ejemplo de test:**
```java
@AnalyzeClasses(packages = "mx.att.digital.api")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_infrastructure =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule controllers_should_be_in_correct_package =
        classes()
            .that().haveNameMatching(".*Controller")
            .should().resideInAPackage("..controllers..");
}
```

### 2. Pitest (Mutation Testing)
```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.17.3</version>
    <dependencies>
        <dependency>
            <groupId>org.pitest</groupId>
            <artifactId>pitest-junit5-plugin</artifactId>
            <version>1.2.1</version>
        </dependency>
    </dependencies>
    <configuration>
        <targetClasses>
            <param>mx.att.digital.api.*</param>
        </targetClasses>
        <targetTests>
            <param>mx.att.digital.api.*</param>
        </targetTests>
        <outputFormats>
            <outputFormat>XML</outputFormat>
            <outputFormat>HTML</outputFormat>
        </outputFormats>
        <mutationThreshold>75</mutationThreshold>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>mutationCoverage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Ejecutar**: `mvn pitest:mutationCoverage`

### 3. REST Assured (Tests de API)
```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>spring-mock-mvc</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>
```

**Ejemplo:**
```java
@WebMvcTest(PaymentController.class)
class PaymentControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPayment_should_return_200() {
        given()
            .mockMvc(mockMvc)
            .when()
            .get("/paymentManagement/v5/payment/{id}", "PAY-000123")
            .then()
            .statusCode(200)
            .body("id", equalTo("PAY-000123"))
            .body("status", equalTo("Accepted"));
    }
}
```

### 4. Testcontainers (Tests de Integración)
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
```

### 5. Awaitility (Ya incluido ✅)
Para tests asíncronos más robustos

### 6. AssertJ (Ya incluido ✅)
Mejores assertions

### 7. Contract Testing con Pact
```xml
<dependency>
    <groupId>au.com.dius.pact.consumer</groupId>
    <artifactId>junit5</artifactId>
    <version>4.7.3</version>
    <scope>test</scope>
</dependency>
```

## 🔍 Plugins Maven de Análisis

### 1. OWASP Dependency Check
```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>11.2.0</version>
    <configuration>
        <format>ALL</format>
        <failBuildOnCVSS>7</failBuildOnCVSS>
        <suppressionFile>owasp-suppressions.xml</suppressionFile>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Ejecutar**: `mvn dependency-check:check`

### 2. Maven Enforcer Plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.5.0</version>
    <executions>
        <execution>
            <id>enforce-versions</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <requireMavenVersion>
                        <version>[3.8,)</version>
                    </requireMavenVersion>
                    <requireJavaVersion>
                        <version>[21,)</version>
                    </requireJavaVersion>
                    <bannedDependencies>
                        <excludes>
                            <exclude>commons-logging</exclude>
                            <exclude>log4j:log4j</exclude>
                        </excludes>
                    </bannedDependencies>
                    <dependencyConvergence/>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 3. Maven Site Plugin (Reportes)
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-site-plugin</artifactId>
    <version>4.0.0-M16</version>
</plugin>
```

**Agregar en `<reporting>`:**
```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>3.5.3</version>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jxr-plugin</artifactId>
            <version>3.5.0</version>
        </plugin>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.13</version>
        </plugin>
        <plugin>
            <groupId>com.github.spotbugs</groupId>
            <artifactId>spotbugs-maven-plugin</artifactId>
            <version>4.8.6.4</version>
        </plugin>
    </plugins>
</reporting>
```

**Generar sitio**: `mvn site`

## 📊 CI/CD Pipeline Checks

### GitHub Actions / GitLab CI
```yaml
# .github/workflows/quality.yml
name: Quality Checks

on: [push, pull_request]

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'

      - name: Run tests with coverage
        run: mvn clean verify

      - name: SonarCloud Analysis
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: mvn sonar:sonar

      - name: SpotBugs
        run: mvn spotbugs:check

      - name: CheckStyle
        run: mvn checkstyle:check

      - name: PMD
        run: mvn pmd:check

      - name: OWASP Dependency Check
        run: mvn dependency-check:check

      - name: Mutation Testing
        run: mvn pitest:mutationCoverage
```

## 🎯 Comandos Útiles

```bash
# Análisis completo
mvn clean verify sonar:sonar spotbugs:check checkstyle:check pmd:check

# Solo tests con cobertura
mvn clean test jacoco:report

# Ver reporte de cobertura
start target/site/jacoco/index.html

# Mutation testing
mvn pitest:mutationCoverage

# Dependency check
mvn dependency-check:check

# Generar sitio con todos los reportes
mvn clean site

# Ver sitio generado
start target/site/index.html
```

## 📈 Métricas Recomendadas

| Métrica | Target Mínimo | Target Ideal |
|---------|---------------|--------------|
| Line Coverage | 80% | 85%+ |
| Branch Coverage | 70% | 80%+ |
| Mutation Score | 60% | 75%+ |
| Code Smells | < 20 | 0 |
| Bugs | 0 | 0 |
| Vulnerabilities | 0 | 0 |
| Security Hotspots | 0 reviewed | 0 |
| Duplications | < 3% | < 1% |
| Complexity | < 15 per method | < 10 |

## 🔒 Pre-commit Hooks

Instalar **Husky** para Java o usar **pre-commit** framework:

```bash
# .git/hooks/pre-commit
#!/bin/bash
mvn clean test
if [ $? -ne 0 ]; then
    echo "Tests failed. Commit aborted."
    exit 1
fi

mvn spotbugs:check checkstyle:check
if [ $? -ne 0 ]; then
    echo "Quality checks failed. Commit aborted."
    exit 1
fi
```

## 🎓 Mejores Prácticas

1. **Ejecutar análisis localmente** antes de push
2. **Revisar reportes de SonarCloud** después de cada push
3. **Mantener 0 bugs críticos** siempre
4. **Corregir security hotspots** inmediatamente
5. **Revisar mutation testing** para mejorar tests
6. **Actualizar dependencias** regularmente (usar Renovate)
7. **Code review** obligatorio antes de merge

## 🆘 Soporte

- SonarCloud: https://sonarcloud.io
- SpotBugs: https://spotbugs.github.io
- ArchUnit: https://www.archunit.org
- Pitest: https://pitest.org
