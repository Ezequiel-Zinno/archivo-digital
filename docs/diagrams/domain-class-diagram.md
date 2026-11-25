# Diagrama de clases de dominio

Capa: `model`.

```mermaid
classDiagram
    class Piece {
        +Long id
        +String name
        +String description
        +LocalDate date
    }

    class Digitization {
        +Long id
        +String url
        +String description
    }

    class Tag {
        +Long id
        +String name
        +boolean active
        +activate()
        +deactivate()
    }

    class Cataloging {
        +Long id
        +validateCanBeCreated()
        +validateDoesNotAlreadyExist(boolean)
    }

    class TagAlreadyCatalogingedException
    class InactiveTagException
    class DuplicateTagNameException

    Piece "1" --> "*" Digitization : has
    Digitization "1" --> "*" Cataloging : catalogings
    Tag "1" --> "*" Cataloging : catalogings
    Cataloging --> Tag : tag
    Cataloging ..> TagAlreadyCatalogingedException : throws
    Cataloging ..> InactiveTagException : throws
    TagService ..> DuplicateTagNameException : throws
```

## Relaciones

- Una `Piece` tiene muchas `Digitization` (1..*).
- Una `Digitization` tiene muchas `Cataloging` (1..*).
- Un `Tag` participa en muchas `Cataloging` (1..*).
- `Cataloging` es la entidad asociativa (única por par digitalización + tag).
  En español: catalogación.
