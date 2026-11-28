# Pruebas del sistema — Archivo Digital

## 1. Preparación del entorno

Requisitos:

- Java 21.
- Maven o Maven Wrapper.
- Token de Telegram para las pruebas del bot.

Ejecutar la aplicación desde la raíz del proyecto.

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux:

```bash
./mvnw spring-boot:run
```

La API estará disponible en http://localhost:8080.

El perfil local utiliza H2 en memoria, por lo que los datos
se eliminan al detener la aplicación.

Las siguientes pruebas parten de una base de datos vacía.
Los comandos de la API se ejecutan desde otra terminal.

## 2. Registro de piezas

Crear una pieza:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/pieces" -ContentType "application/json" -Body '{"name":"Fotografia antigua","description":"Fotografia historica de Buenos Aires","date":"1920-05-15"}'
```

Resultado esperado: HTTP 201 y pieza creada con ID 1.

Consultar las piezas registradas:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces"
```

Resultado esperado: HTTP 200 y listado de piezas.

## 3. Registro de digitalizaciones

Crear una digitalización asociada a la pieza anterior:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/digitizations" -ContentType "application/json" -Body '{"url":"https://example.com/fotografia.jpg","description":"Digitalizacion de fotografia antigua","pieceId":1}'
```

Resultado esperado: HTTP 201 y digitalización asociada
a la pieza con ID 1.

Consultar las digitalizaciones:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/digitizations"
```

Resultado esperado: HTTP 200 y listado de digitalizaciones.

## 4. Gestión de tags

Crear un tag:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/tags" -ContentType "application/json" -Body '{"name":"archivo"}'
```

Resultado esperado: HTTP 201 y tag activo con ID 1.

Consultar los tags:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/tags"
```

Resultado esperado: HTTP 200 y listado de tags.

## 5. Catalogación

Asociar el tag a la digitalización:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/digitizations/1/catalogings" -ContentType "application/json" -Body '{"tagId":1}'
```

Resultado esperado: HTTP 201 y asociación creada.

Consultar los tags asociados:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/digitizations/1/catalogings"
```

Resultado esperado: HTTP 200 y listado de asociaciones.

## 6. Búsqueda de piezas

### Por nombre

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?name=foto"
```

### Por descripción

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?description=Buenos%20Aires"
```

### Por fecha

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?date=1920-05-15"
```

### Por tag

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?tag=archivo"
```

### Combinación de filtros

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?name=foto&description=Buenos%20Aires&date=1920-05-15&tag=archivo"
```

Resultado esperado: HTTP 200 y la pieza registrada
en cada una de las búsquedas anteriores.

### Búsqueda sin coincidencias

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?name=avion"
```

Resultado esperado: HTTP 200 y lista vacía.

### Combinación sin coincidencias

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?name=foto&tag=deportes"
```

Resultado esperado: HTTP 200 y lista vacía.

## 7. Activación y desactivación de tags

Desactivar el tag:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/tags/1/deactivate"
```

Resultado esperado: HTTP 200 y active = false.

Verificar que se conserva la catalogación existente:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?tag=archivo"
```

Resultado esperado: la pieza sigue disponible mediante
la búsqueda por el tag previamente asociado.

Reactivar el tag:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/tags/1/activate"
```

Resultado esperado: HTTP 200 y active = true.

## 8. Validaciones y manejo de errores

### Pieza con nombre vacío

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/pieces" -ContentType "application/json" -Body '{"name":"","description":"Pieza de prueba","date":"1920-05-15"}'
```

Resultado esperado: HTTP 400.

### Digitalización asociada a una pieza inexistente

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/digitizations" -ContentType "application/json" -Body '{"url":"https://example.com/prueba.jpg","description":"Digitalizacion de prueba","pieceId":999}'
```

Resultado esperado: HTTP 404.

### Catalogación con un tag inexistente

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/digitizations/1/catalogings" -ContentType "application/json" -Body '{"tagId":999}'
```

Resultado esperado: HTTP 404.

### Catalogación duplicada

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/digitizations/1/catalogings" -ContentType "application/json" -Body '{"tagId":1}'
```

Resultado esperado: HTTP 409.

### Catalogación con un tag inactivo

Crear un segundo tag:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/tags" -ContentType "application/json" -Body '{"name":"prueba"}'
```

Desactivarlo:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/tags/2/deactivate"
```

Intentar asociarlo a la digitalización:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/digitizations/1/catalogings" -ContentType "application/json" -Body '{"tagId":2}'
```

Resultado esperado: HTTP 400.

### Formato de fecha incorrecto

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/pieces/search?date=15-05-1920"
```

Resultado esperado: HTTP 400.

## 9. Bot de Telegram

Para realizar estas pruebas, iniciar la aplicación con
las variables de entorno TELEGRAM_BOT_TOKEN y
TELEGRAM_BOT_USERNAME configuradas según el README.md.

No guardar el token en el repositorio.

Si se reinicia la aplicación, volver a cargar los datos
de prueba de las secciones 2 a 5.

Ingresar al bot @timbero02_bot y enviar los siguientes
mensajes.

| Mensaje | Resultado esperado |
|---|---|
| `/start` | Instrucciones de uso |
| `/ayuda` | Instrucciones de búsqueda |
| `foto` | Pieza encontrada |
| `/buscar nombre=foto` | Pieza encontrada |
| `/buscar descripcion=Buenos Aires` | Pieza encontrada |
| `/buscar fecha=1920-05-15` | Pieza encontrada |
| `/buscar tag=archivo` | Pieza encontrada |
| `/buscar tag=deportes` | Sin coincidencias |
| `/buscar nombre=` | Error de filtro vacío |
| `/buscar color=rojo` | Error de filtro desconocido |
| `/buscar fecha=15-05-1920` | Error de formato de fecha |

Probar también la combinación de todos los filtros:

```text
/buscar nombre=foto | descripcion=Buenos Aires | tag=archivo | fecha=1920-05-15
```

Resultado esperado: una pieza encontrada, mostrando
su nombre, fecha y descripción.

## 10. Desasociación de tags

Eliminar la asociación entre la digitalización y el tag:

```powershell
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/digitizations/1/catalogings/1"
```

Resultado esperado: HTTP 204.

Consultar nuevamente las asociaciones:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/digitizations/1/catalogings"
```

Resultado esperado: HTTP 200 y lista vacía.

## 11. Pruebas automatizadas

Windows:

```powershell
.\mvnw.cmd test
```

Linux:

```bash
./mvnw test
```

Último resultado de ejecución:

| Clase | Cantidad de pruebas |
|---|---:|
| TagTest | 3 |
| CatalogingServiceTest | 8 |
| DigitizationServiceTest | 5 |
| PieceSearchServiceTest | 3 |
| PieceServiceTest | 4 |
| TagServiceTest | 7 |
| TelegramSearchBotServiceTest | 8 |
| TpApplicationTests | 1 |
| Total | 39 |

- Pruebas ejecutadas: 39.
- Fallos: 0.
- Errores: 0.
- Pruebas omitidas: 0.
- Resultado: BUILD SUCCESS.

## 12. Controles de calidad

Windows:

```powershell
.\mvnw.cmd checkstyle:check
.\mvnw.cmd pmd:check
.\mvnw.cmd spotbugs:check
```

Linux:

```bash
./mvnw checkstyle:check
./mvnw pmd:check
./mvnw spotbugs:check
```

Resultados de la última ejecución:

| Herramienta | Resultado |
|---|---|
| Checkstyle | 0 infracciones |
| PMD | BUILD SUCCESS |
| SpotBugs | 0 errores y 0 problemas detectados |

## 13. Referencias

- [README.md](README.md)
- [Documentación de la API](docs/api.md)
- [Arquitectura del sistema](ARCHITECTURE.md)