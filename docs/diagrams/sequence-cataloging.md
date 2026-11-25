# Diagrama de secuencia — Asociar tag a una digitalización

Flujo: `POST /api/digitizations/{id}/catalogings` con `{ "tagId": 1 }`.

Capas (Fowler): Controller → Service → Repository → Entity (model).

```mermaid
sequenceDiagram
    participant C as Controller<br/>CatalogingController
    participant S as Service<br/>CatalogingService
    participant R as Repository<br/>CatalogingRepository
    participant DR as Repository<br/>DigitizationRepository
    participant TR as Repository<br/>TagRepository
    participant E as Entity<br/>Cataloging / Tag

    C->>S: catalog(digitizationId, request)
    S->>DR: findById(digitizationId)
    DR-->>S: Digitization (o vacío → 404)
    S->>TR: findById(tagId)
    TR-->>S: Tag (o vacío → 404)
    S->>E: Cataloging.of(digitization, tag)
    E->>E: validateCanBeCreated()<br/>Tag activo? si no → 400
    S->>R: existsByDigitizationIdAndTagId(...)
    R-->>S: boolean
    S->>E: validateDoesNotAlreadyExist(alreadyExists)
    E-->>S: si ya existe → 409
    S->>R: save(cataloging)
    R-->>S: Cataloging persistida
    S-->>C: CatalogingResponseDTO
    C-->>HTTP: 201 Created
```
