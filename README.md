# Arquetipo - Management


Servicio de gestión de ... , implementado con Spring Boot 3.5.3 y Java 21.

## Índice
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Requisitos Previos](#requisitos-previos)
- [Tecnologías](#tecnologias)
- [Construcción](#construccion)
- [API REST](#api-rest)
- [Tests](#tests)
- [Docker](#docker)


## Estructura del Proyecto

El proyecto está organizado en los siguientes módulos:

- `infrastructure/in/micro-management-api-rest`: API REST generada automáticamente a partir de una especificación OpenAPI.
- `domain`: Lógica de dominio, estructuras y modelos.
- `application`: Casos de uso, manejo de exceptiones globales y lógica de negocio.
- `boot`: Módulo de arranque y configuración.

## Requisitos Previos

- Java 21
- Maven 3.9.9

## Tecnologías

- Spring Boot 3.5.3
- MapStruct 1.6.3
- Lombok 1.18.38
- JUnit 5
- Mockito
- Jackson 2.14.2
- Postgres 17.5

## Construcción

Ejecuta el siguiente comando para compilar y construir el proyecto:

```bash
mvn clean install
```

## API REST

La API REST se genera automáticamente a partir de la especificación OpenAPI, ubicada en:
`${project.basedir}/contracts/swagger.json`

En caso de requerir generar otro API TMF, descargar su defición en yaml o json y reemplazar este archivo en contracts. Finalmente ajustar el POM por el nombre correcto del swagger

## Tests

Para ejecutar los tests:

```bash
mvn test
```

## Docker

Este proyecto incluye un archivo `Dockerfile` para compilar y ejecutar la aplicación en un contenedor Docker mediante una build multi-etapa.

### Construir la imagen Docker

Utiliza el siguiente comando para construir la imagen. Se asume que el archivo `Dockerfile` se encuentra en la raíz del proyecto.

```bash
docker build -t micro-management .
```

### Ejecutar el contenedor

Después de construir la imagen, ejecuta el siguiente comando para levantar un contenedor, exponiendo el puerto 8080:

```bash
docker run -p 16600:16600 micro-management
```

### Levantar solución completa

Levantar los servicios definidos en docker-compose.yaml, construyendo las imágenes app, servicios externos mocks y base de datos desde cero si es necesario (-- build es importante para asegurar que los contenedores se basan en la última versión del código o tome algún cambio en Dockerfile)

Consideración importante dar valor a las variables de entorno según ambiente:

```bash
"Env": [
	TMFXXX_DATASOURCE_DB=tmforumDominioServicioDB
	TMFXXX_DATASOURCE_PASSWORD=V9p!zL#7tQ@eF2hWk3
	TMFXXX_DATASOURCE_SCHEMA=dominioServicio
	TMFXXX_DATASOURCE_TIMEOUT=5000
	TMFXXX_DATASOURCE_USERNAME=usr_tmforumXXX
	TMFXXX_LOG_FILE_NAME=/opt/apps/logs/AgreementManagement/agreement-management.log
	TMFXXX_LOG_FILE_PATH=/opt/apps/logs/AgreementManagement
	TMFXXX_SERVER_PORT=16600
	TMFXXX_SPRING_SECURITY_USER_NAME=tmforumXXX
	TMFXXX_SPRING_SECURITY_USER_PASSWORD={bcrypt}$2a$12$IPwc3N0Pzjs9P3KHVlDFvO5GX02dB71tczbqyVEbpZ0eMt4D9cdSG
	TMFXXX_TOPUP_SERVICE_URL=http://localhost:3000
	TMFXXX_TOPUP_URI_REVERSE=/doReverse
	TMFXXX_TOPUP_URI_TOPUP=/doTopup
	TMFXXX_TOPUPHUB_CANCEL_TIME=90
	TMFXXX_TOPUPHUB_PASSWORD=4P19p!zL#7tQ@eF2hPz$
	TMFXXX_TOPUPHUB_TIME_UNIQUE_COMBINATION=90
	TMFXXX_TOPUPHUB_USER=tmfAPIXXX
] 
```
 
Comando para levantar

```bash
docker compose -f 'docker-compose.yaml' up -d --build 
```

### 🔐 Flujo con los secretos

Especificación de **secrets** manejarlos por medio del servicio de AWS CM


---
### Proceso para generar y usar el arquetipo (TM Forum AT&T)


> **Resumen:**
> 1. Clonar e instalar → 2. Crear arquetipo → 3. Instalar arquetipo → 4. Generar nuevo proyecto




**1. Clonar el repositorio e instalar el proyecto rama master**

```bash
git clone https://github.com/em9421_ATT/tmforum-base
cd <carpeta-del-proyecto>
mvn install
```


**2. Crear el arquetipo a partir del proyecto existente**

Ejecuta el siguiente comando en la raíz del proyecto principal:

```bash
mvn archetype:create-from-project
```

Este objetivo genera un arquetipo basado en el contenido del proyecto, transformando sus fuentes, recursos y metadatos en plantillas Velocity 
Apache Maven


**3. Instalar el arquetipo generado**

Navega hasta la carpeta del arquetipo generado y procede a limpiar y construir el proyecto:

```bash
cd target/generated-sources/archetype
mvn clean install
```

Esto instalará el arquetipo en tu repositorio local, permitiéndote utilizarlo localmente.


**4. Generar nuevos proyectos desde el arquetipo**

Desde la ruta deseada, ejecuta un comando como el siguiente para crear un nuevo proyecto ej: agreement-management 

```bash
mvn archetype:generate \
  -DarchetypeGroupId=mx.att.digital.api \
  -DarchetypeArtifactId=management-archetype \
  -DarchetypeVersion=0.0.1 \
  -DgroupId=mx.att.digital.api \
  -DartifactId=agreement-management \
  -Dversion=0.0.1 \
  -Dpackage=mx.att.digital.api.tmf651
```
```bash
mvn archetype:generate -DarchetypeGroupId=mx.att.digital.api -DarchetypeArtifactId=management-archetype -DarchetypeVersion=0.0.1 -DgroupId=mx.att.digital.api -DartifactId=payment-management -Dversion=0.0.1 -Dpackage=mx.att.digital.api.tmf676
```

*Opcional: Para crear un proyecto resource-inventory:*

```bash
mvn archetype:generate \
  -DarchetypeGroupId=mx.att.digital.api \
  -DarchetypeArtifactId=management-archetype \
  -DarchetypeVersion=0.0.1 \
  -DgroupId=mx.att.digital.api \
  -DartifactId=resource-inventory-management \
  -Dversion=0.0.1 \
  -Dpackage=mx.att.digital.api.tmf639 \
  -DinteractiveMode=false
```

Este comando permite la generación manual sin preguntas adicionales (modo no interactivo) 




## TMF676 mock, tests y openapi SIN perfiles
- `management-api-rest` genera código OpenAPI en **generate-sources** siempre (sin `-Ptmforum`).
- Mocks y tests listos para cobertura ≥80% con JaCoCo.
Variables para el conector GET token
<!-- 
$env:PAYMENTSPORTAL_CONNECTOR_BASEURL = "http://localhost:9090"
$env:PAYMENTSPORTAL_CONNECTOR_USERNAME="admin"
$env:PAYMENTSPORTAL_CONNECTOR_PASSWORD="admin"
curl --location "http://localhost:16600/paymentManagement/v4/token"
-->

$env:PAYMENTSPORTAL_CONNECTOR_BASEURL = "https://coremgmtdev.pre-prod.mx.att.com/paymentsportal-connector"
$env:PAYMENTSPORTAL_CONNECTOR_USERNAME="admin"
$env:PAYMENTSPORTAL_CONNECTOR_PASSWORD="admin"
curl --location "https://coremgmtdev.pre-prod.mx.att.com/paymentManagement/v4/token"

Build:
```
mvn clean package -DskipTests
mvn -U -DskipITs clean verify
java -jar boot\target\ms-api-tmf676-pymt-mgt.jar --server.port=16600
```
