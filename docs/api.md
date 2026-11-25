# API — Archivo Digital

Especificación de la API web. Nombres de dominio alineados con el equipo:
`Piece` (pieza), `Digitization` (digitalización), `Tag`, `Cataloging` (asociación
Digitization ↔ Tag; en español: catalogación).

Base path: `http://localhost:8080`

Formato de request/response: JSON (`Content-Type: application/json`).

Errores: cuerpo `{"error": "<mensaje>"}`.

---

## Tags

El administrador puede cambiar el conjunto de tags en tiempo de ejecución.
Los tags no se borran físicamente: se desactivan (`active = false`).

### Crear tag

```
POST /api/tags
```

Request:

```json
{ "name": "foto" }
```

| Código | Descripción |
|--------|-------------|
| 201 | Creado. Devuelve el tag |
| 400 | `name` vacío o ausente |
| 409 | Ya existe un tag con ese nombre |

Response `201`:

```json
{ "id": 1, "name": "foto", "active": true }
```

### Listar tags

```
GET /api/tags
GET /api/tags?active=true
```

| Código | Descripción |
|--------|-------------|
| 200 | Lista de tags (todos, o solo los activos con `active=true`) |

Response `200`:

```json
[
  { "id": 1, "name": "foto", "active": true },
  { "id": 2, "name": "plano", "active": false }
]
```

### Obtener tag

```
GET /api/tags/{id}
```

| Código | Descripción |
|--------|-------------|
| 200 | Tag encontrado |
| 404 | Tag inexistente |

### Activar / desactivar tag

```
POST /api/tags/{id}/activate
POST /api/tags/{id}/deactivate
```

| Código | Descripción |
|--------|-------------|
| 200 | Tag actualizado |
| 404 | Tag inexistente |

Response `200`:

```json
{ "id": 1, "name": "foto", "active": false }
```

---

## Cataloging (catalogación)

Asocia tags a una digitalización. Cada par `(digitization, tag)` es único.
Solo se pueden asociar tags **activos**.

### Asociar tag a una digitalización

```
POST /api/digitizations/{digitizationId}/catalogings
```

Request:

```json
{ "tagId": 1 }
```

| Código | Descripción |
|--------|-------------|
| 201 | Asociación creada |
| 400 | Tag inactivo |
| 404 | Digitalización o tag inexistente |
| 409 | El tag ya está asociado a esa digitalización |

Response `201`:

```json
{ "id": 10, "digitizationId": 5, "tag": { "id": 1, "name": "foto", "active": true } }
```

### Desasociar tag

```
DELETE /api/digitizations/{digitizationId}/catalogings/{tagId}
```

| Código | Descripción |
|--------|-------------|
| 204 | Asociación eliminada (sin cuerpo) |
| 404 | Digitalización, tag o asociación inexistente |

### Tags de una digitalización

```
GET /api/digitizations/{digitizationId}/catalogings
```

| Código | Descripción |
|--------|-------------|
| 200 | Lista de asociaciones |
| 404 | Digitalización inexistente |

Response `200`:

```json
[
  { "id": 10, "digitizationId": 5, "tag": { "id": 1, "name": "foto", "active": true } }
]
```

---

## Capas

Cada endpoint sigue: `controller` → `service` → `repository` → entidad de `model`.
El manejo de errores HTTP vive en `exception.GlobalExceptionHandler`.
