# Diagrama de clases de dominio

Capa: `model`.

```mermaid
classDiagram
    class Piece {
        -Long id
        -String name
        -String description
        -LocalDate date
    }

    class Digitization {
        -Long id
        -String url
        -String description
        -Piece piece
        -Set~Cataloging~ catalogings
    }

    class Tag {
        -Long id
        -String name
        -boolean active
        +activate()
        +deactivate()
    }

    class Cataloging {
        -Long id
        -Digitization digitization
        -Tag tag
        +of(digitization, tag)$ Cataloging
        +validateCanBeCreated()
        +validateDoesNotAlreadyExist(boolean)
    }

    class TagAlreadyCatalogingedException
    class InactiveTagException

    Digitization "0..*" --> "1" Piece : piece
    Digitization "1" -- "0..*" Cataloging : catalogings
    Cataloging "0..*" --> "1" Tag : tag

    Cataloging ..> TagAlreadyCatalogingedException : throws
    Cataloging ..> InactiveTagException : throws
```

## Relaciones

- Una `Piece` puede tener cero o muchas digitalizaciones.
- Cada `Digitization` pertenece a una única pieza.
- Una `Digitization` puede tener cero o muchas catalogaciones.
- Cada `Cataloging` pertenece a una única digitalización
  y referencia un único tag.
- Un `Tag` puede participar en cero o muchas catalogaciones.
- `Cataloging` es la entidad asociativa entre una
  digitalización y un tag.

## Reglas de dominio

- No se puede catalogar una digitalización con un tag inactivo.
- No se puede repetir una asociación entre una digitalización
  y un mismo tag.
- Desactivar un tag no elimina las catalogaciones existentes.
- Los tags pueden activarse y desactivarse.

Los getters, setters y constructores generados mediante
Lombok se omiten del diagrama para facilitar su lectura.