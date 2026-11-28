# Diagrama de secuencia — Asociar tag a una digitalización

Flujo: `POST /api/digitizations/{id}/catalogings`.

Cuerpo de la solicitud: `{ "tagId": 1 }`.

Capas: Controller, Service, Repository y Model.

```mermaid
sequenceDiagram
    actor Client as Cliente REST
    participant C as Controller: CatalogingController
    participant S as Service: CatalogingService
    participant DR as Repository: DigitizationRepository
    participant TR as Repository: TagRepository
    participant E as Model: Cataloging
    participant R as Repository: CatalogingRepository
    participant H as Controller: GlobalExceptionHandler

    Client->>C: POST /api/digitizations/{id}/catalogings
    C->>S: catalog(digitizationId, request)
    S->>DR: findById(digitizationId)
    DR-->>S: Optional de Digitization

    alt Digitalización inexistente
        S-->>H: DigitizationNotFoundException
        H-->>Client: 404 Not Found
    else Digitalización existente
        S->>TR: findById(tagId)
        TR-->>S: Optional de Tag

        alt Tag inexistente
            S-->>H: TagNotFoundException
            H-->>Client: 404 Not Found
        else Tag existente
            S->>E: of(digitization, tag)
            E->>E: validateCanBeCreated()

            alt Tag inactivo
                E-->>S: InactiveTagException
                S-->>H: Propaga excepción
                H-->>Client: 400 Bad Request
            else Tag activo
                E-->>S: Cataloging
                S->>R: existsByDigitizationIdAndTagId(...)
                R-->>S: boolean
                S->>E: validateDoesNotAlreadyExist(alreadyExists)

                alt Asociación duplicada
                    E-->>S: TagAlreadyCatalogingedException
                    S-->>H: Propaga excepción
                    H-->>Client: 409 Conflict
                else Asociación válida
                    E-->>S: Validación exitosa
                    S->>R: save(cataloging)
                    R-->>S: Cataloging persistida
                    S-->>C: CatalogingResponseDTO
                    C-->>Client: 201 Created
                end
            end
        end
    end
```

## Responsabilidades

- Controller: recibe la solicitud y devuelve la respuesta HTTP.
- Service: coordina la operación dentro de una transacción.
- Repository: consulta y persiste las entidades.
- Model: valida las reglas de negocio de la catalogación.
- GlobalExceptionHandler: traduce las excepciones a
  respuestas HTTP mediante Spring MVC.

Las flechas hacia GlobalExceptionHandler representan el
manejo de excepciones realizado por Spring MVC, no
invocaciones directas desde el servicio.