# Diagrama de secuencia — Crear una pieza

Flujo: `POST /api/pieces`.

```mermaid
sequenceDiagram
    actor Client as Cliente REST
    participant C as Controller: PieceController
    participant S as Service: PieceService
    participant E as Model: Piece
    participant R as Repository: PieceRepository
    participant DB as Base de datos H2

    Client->>C: POST /api/pieces
    C->>S: createPiece(request)
    S->>E: Crear instancia de Piece
    S->>E: setName, setDescription, setDate
    S->>R: save(piece)
    R->>DB: INSERT en pieces
    DB-->>R: Registro persistido
    R-->>S: Piece con ID asignado
    S->>S: toResponse(piece)
    S-->>C: PieceResponseDTO
    C-->>Client: 201 Created
```

## Responsabilidades

- Controller: recibe y valida la solicitud HTTP.
- Service: coordina la creación de la pieza.
- Model: representa la entidad del dominio.
- Repository: gestiona la persistencia mediante JPA.

La operación de creación se ejecuta dentro de una
transacción mediante `@Transactional`.

El diagrama representa el flujo exitoso.