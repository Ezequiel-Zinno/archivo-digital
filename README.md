# Archivo Digital — FC Timba

Trabajo práctico de Diseño y Desarrollo de Software — UNSAM.

## Integrantes

| Nombre y apellido | Usuario de GitHub |
|-------------------|-------------------|
| Ezequiel Zinno | [Ezequiel-Zinno](https://github.com/Ezequiel-Zinno) |
| Juan Pablo Fernandez | [juanpyfernandez](https://github.com/juanpyfernandez) |
| Agustin Monzon | [romonzon30](https://github.com/romonzon30) |

## Descripción

Archivo Digital es una aplicación que permite registrar piezas, gestionar sus digitalizaciones y organizarlas mediante etiquetas para facilitar su catalogación, con búsqueda de piezas y consulta mediante un bot de Telegram.

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Maven
- H2
- JUnit y Mockito
- Git y GitHub

## Funcionalidades implementadas

- Creación y consulta de piezas.
- Búsqueda de piezas por nombre, descripción, fecha y etiqueta.
- Creación y consulta de digitalizaciones.
- Asociación de digitalizaciones con sus respectivas piezas.
- Creación, consulta, activación y desactivación de etiquetas.
- Catalogación de digitalizaciones mediante etiquetas.
- Integración con bot de Telegram (@timbero02_bot) para buscar piezas.
- Validación de datos y manejo de excepciones.
- Pruebas automatizadas.

## Estructura del proyecto

El proyecto utiliza una arquitectura en capas:

src/main/java/ar/edu/unsam/ddso/tp/
├── controller/
├── dto/
├── exception/
├── model/
├── repository/
├── service/
└── TpApplication.java

src/main/resources/
├── application.properties
├── application-local.properties
└── application-prod.properties

src/test/java/ar/edu/unsam/ddso/tp/
├── model/
├── service/
└── TpApplicationTests.java

docs/
├── diagrams/
└── api.md

## Requisitos

- Java 21
- Maven o Maven Wrapper

## Cómo correr los tests

Linux:

./mvnw test

Windows:

.\mvnw.cmd test

## Cómo ejecutar la app en local

Configurá las variables de entorno del bot de Telegram antes de iniciar (sin guardar el token en el repositorio):

- TELEGRAM_BOT_TOKEN: token del bot obtenido con BotFather.
- TELEGRAM_BOT_USERNAME: usuario del bot (opcional, por defecto timbero02_bot).

En PowerShell (Windows):

$env:TELEGRAM_BOT_TOKEN="tu_token_aqui"
$env:TELEGRAM_BOT_USERNAME="timbero02_bot"
.\mvnw.cmd spring-boot:run

En Linux:

export TELEGRAM_BOT_TOKEN="tu_token_aqui"
export TELEGRAM_BOT_USERNAME="timbero02_bot"
./mvnw spring-boot:run

La aplicación se ejecuta en:

http://localhost:8080

Perfil local por defecto, con base de datos H2 en memoria.

Consola H2: http://localhost:8080/h2-console

## Uso del bot de Telegram

Una vez levantada la aplicación con TELEGRAM_BOT_TOKEN configurado:

- /start o /ayuda: muestra las instrucciones de búsqueda.
- Texto libre (por ejemplo foto): busca piezas coincidentes.
- Filtros separados por |: /buscar nombre=foto | tag=archivo | fecha=1920-05-15.

## Documentación de la API

La documentación de los endpoints, formatos JSON y códigos de respuesta HTTP se encuentra en [docs/api.md](docs/api.md).

## Pruebas funcionales

La secuencia de pruebas manuales de la API REST y del bot de Telegram
se encuentra documentada en [test.md](test.md).
