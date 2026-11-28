# Diagrama de secuencia — Crear una digitalización

Flujo: `POST /api/digitizations`.

```mermaid
sequenceDiagram
    actor Client as Cliente REST
    participant C as Controller: DigitizationController
    participant S as Service: DigitizationService
    participant PR as Repository: PieceRepository
    participant E as Model: Digitization
    participant DR as Repository: DigitizationRepository
    participant H as Controller: GlobalExceptionHandler

    Client->>C: POST /api/digitizations
    C->>S: createDigitization(request)
    S->>PR: findById(request.pieceId())
    PR-->>S: Optional de Piece

    alt Pieza inexistente
        S-->>H: PieceNotFoundException
        H-->>Client: 404 Not Found
    else Pieza existente
        S->>E: Crear instancia de Digitization
        S->>E: setUrl, setDescription, setPiece
        S->>DR: save(digitization)
        DR-->>S: Digitalización persistida
        S->>S: toResponse(digitization)
        S-->>C: DigitizationResponseDTO
        C-->>Client: 201 Created
    end
```

## Responsabilidades

- Controller: recibe y valida la solicitud HTTP.
- Service: verifica la existencia de la pieza y coordina
  la creación de la digitalización.
- Model: representa la digitalización y su relación
  con la pieza física.
- Repository: consulta y persiste las entidades.
- GlobalExceptionHandler: traduce las excepciones
  a respuestas HTTP mediante Spring MVC.

No se puede crear una digitalización asociada a una
pieza inexistente.

La operación se ejecuta dentro de una transacción.

El manejo de excepciones es realizado por Spring MVC,
no mediante una llamada directa al GlobalExceptionHandler.