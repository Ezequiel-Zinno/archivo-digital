# Archivo Digital — FC Timba

Trabajo práctico de Diseño y Desarrollo de Software — UNSAM.

## Integrantes

| Nombre y apellido | Usuario de GitHub |
|-------------------|-------------------|
| Ezequiel Zinno | [Ezequiel-Zinno](https://github.com/Ezequiel-Zinno) |
| Juan Pablo Fernandez | [juanpyfernandez](https://github.com/juanpyfernandez) |
| Agustin Monzon | [romonzon30](https://github.com/romonzon30) |

## Descripción

Archivo Digital es una aplicación que permite registrar piezas, gestionar sus digitalizaciones y organizarlas mediante etiquetas para facilitar su catalogación.

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
- Creación y consulta de digitalizaciones.
- Asociación de digitalizaciones con sus respectivas piezas.
- Creación, consulta, activación y desactivación de etiquetas.
- Catalogación de digitalizaciones mediante etiquetas.
- Validación de datos y manejo de excepciones.
- Pruebas automatizadas.

## Estructura del proyecto

El proyecto utiliza una arquitectura en capas:

```
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
```

## Requisitos

- Java 21
- Maven o Maven Wrapper

## Cómo correr los tests

Linux:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

## Cómo ejecutar la app en local

Linux:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación se ejecuta en:

http://localhost:8080

Perfil `local` por defecto, con base de datos H2 en memoria.

Consola H2: http://localhost:8080/h2-console

## Documentación de la API

La documentación de los endpoints, formatos JSON y códigos de respuesta HTTP se encuentra en [docs/api.md](docs/api.md).

## Funcionalidades pendientes

- Búsqueda de contenido del archivo digital.
- Integración con un bot de Telegram.
