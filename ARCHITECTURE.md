# ARCHITECTURE.md

Documento transversal que complementa a `AGENTS.md`
con la descripción del dominio y la arquitectura general
del sistema.

## 1. Descripción general

Archivo Digital es una aplicación monolítica desarrollada
con Java 21, Spring Boot y H2.

Permite registrar piezas físicas, asociar digitalizaciones,
administrar tags, catalogar digitalizaciones y buscar piezas.

El sistema ofrece una API REST HTTP/JSON y una integración
con Telegram para realizar búsquedas y mostrar resultados.

## 2. Actores y sistemas

- Administrador: gestiona los tags y realiza catalogaciones.
- Cliente REST: registra y consulta piezas y digitalizaciones.
- Usuario de Telegram: busca piezas mediante el bot.
- API de Telegram: servicio externo utilizado para recibir
  mensajes y enviar respuestas.

## 3. Alcance y fuera de alcance

### Dentro de alcance

- Digitalización: alta de piezas y asociación de recursos
  digitales mediante URL y descripción.
- Catalogación: asociación de tags a digitalizaciones.
- Administración de tags: creación, consulta, activación
  y desactivación.
- Disponibilización: búsqueda de piezas por fecha, nombre,
  descripción y tags.
- Visualización: búsqueda y presentación de resultados
  mediante Telegram.

### Fuera de alcance

- Pantallas o frontend web.
- Procesamiento de imágenes mediante OCR.
- Etiquetado automático.
- Importación masiva mediante CSV.

## 4. Glosario de dominio

| Término | Significado |
|---|---|
| Piece | Pieza física del archivo con nombre, descripción y fecha. |
| Digitization | Recurso digital identificado mediante una URL y asociado a una pieza. |
| Tag | Etiqueta administrable que puede estar activa o inactiva. |
| Cataloging | Asociación única entre una digitalización y un tag. |

El código utiliza nombres en inglés según las convenciones
establecidas en AGENTS.md.

## 5. Arquitectura del sistema

Se utiliza una arquitectura monolítica organizada en capas:

- Presentación: controllers REST.
- Negocio: servicios y entidades del dominio.
- Acceso a datos: repositories mediante Spring Data JPA.

Los DTOs permiten intercambiar información entre la API
y los servicios sin exponer directamente las entidades.

Las excepciones se gestionan mediante GlobalExceptionHandler.

La integración con Telegram forma parte de la misma
aplicación y reutiliza PieceSearchService directamente.

El sistema utiliza una única base de datos H2 en el
perfil local.

Ver [diagramas de arquitectura](docs/diagrams/architecture-diagram.md).

## 6. Diagramas del dominio y persistencia

- [Diagrama de clases de dominio](docs/diagrams/domain-class-diagram.md).
- [Diagrama de base de datos](docs/diagrams/database-diagram.md).

## 7. Diagramas de secuencia

- [Creación de piezas](docs/diagrams/sequence-piece.md).
- [Creación de digitalizaciones](docs/diagrams/sequence-digitization.md).
- [Catalogación](docs/diagrams/sequence-cataloging.md).
- [Búsqueda de piezas](docs/diagrams/sequence-search.md).
- [Búsqueda mediante Telegram](docs/diagrams/sequence-telegram.md).

## 8. Estado de implementación

- Alta y consulta de piezas y digitalizaciones.
- Administración de tags.
- Catalogación y desasociación de tags.
- Conservación de las asociaciones existentes al desactivar tags.
- Búsqueda de piezas mediante filtros combinables.
- Integración con Telegram.
- Validación de datos y manejo de excepciones.
- Pruebas automatizadas y controles de calidad.
- Pruebas funcionales y de volumen.

## 9. Consideraciones de rendimiento

Los filtros de búsqueda se aplican mediante consultas
a la base de datos utilizando JPQL.

Se realizaron pruebas locales con hasta 50000 piezas.

Actualmente no se utiliza paginación, por lo que las
consultas con muchas coincidencias pueden generar
respuestas de mayor tamaño.

Los resultados y las limitaciones de las pruebas se
encuentran documentados en docs/test.md.

## 10. Referencias

- [Convenciones de código](AGENTS.md).
- [Documentación de la API](docs/api.md).
- [Pruebas del sistema](docs/test.md).
- [README del proyecto](README.md).