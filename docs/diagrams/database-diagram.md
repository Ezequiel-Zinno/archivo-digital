# Diagrama de base de datos

Tablas generadas por JPA (perfil `local`, H2 con `ddl-auto=create-drop`).

```mermaid
erDiagram
    pieces ||--o{ digitizations : "piece_id"
    digitizations ||--o{ catalogings : "digitization_id"
    tags ||--o{ catalogings : "tag_id"

    pieces {
        bigint id PK
        varchar name "not null"
        varchar description "not null, 2000"
        date date "not null"
    }

    digitizations {
        bigint id PK
        varchar url "not null, 2000"
        varchar description "not null, 2000"
        bigint piece_id FK "not null"
    }

    tags {
        bigint id PK
        varchar name "not null, unique"
        boolean active "not null"
    }

    catalogings {
        bigint id PK
        bigint digitization_id FK "not null"
        bigint tag_id FK "not null"
    }
```

## Restricciones

- `catalogings`: unique compuesto `(digitization_id, tag_id)` — una sola asociación
  por par digitalización/tag (entidad intermedia de catalogación).
- `tags.name`: unique.
- FKs NOT NULL en `digitizations.piece_id`, `catalogings.digitization_id`,
  `catalogings.tag_id`.
