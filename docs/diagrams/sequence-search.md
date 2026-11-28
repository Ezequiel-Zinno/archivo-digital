# Diagrama de secuencia — Búsqueda de piezas

Flujo: `GET /api/pieces/search`.

Filtros opcionales: nombre, descripción, fecha y tag.

```mermaid
sequenceDiagram
    actor Client as Cliente REST
    participant C as Controller: PieceSearchController
    participant S as Service: PieceSearchService
    participant R as Repository: PieceRepository
    participant DB as Base de datos H2

    Client->>C: GET /api/pieces/search con filtros
    C->>S: searchPieces(name, description, date, tag)
    S->>S: Normalizar filtros
    S->>R: Buscar piezas según los filtros
    R->>DB: Ejecutar consulta JPQL mediante JPA
    DB-->>R: Registros encontrados
    R-->>S: List de Piece
    S->>S: Convertir entidades a DTOs
    S-->>C: List de PieceResponseDTO
    C-->>Client: 200 OK con resultados
```

## Consideraciones

- Los filtros son opcionales y pueden combinarse.
- La búsqueda se realiza sobre las piezas físicas.
- Los filtros se aplican mediante consultas a la base de datos.
- Se utiliza DISTINCT para evitar piezas duplicadas.
- Si no existen coincidencias, se devuelve una lista vacía.
- Actualmente no se utiliza paginación.