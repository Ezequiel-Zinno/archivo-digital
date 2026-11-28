# Diagrama de secuencia — Búsqueda mediante Telegram

El bot reutiliza el servicio de búsqueda de la aplicación
sin realizar solicitudes HTTP a nuestra propia API REST.

```mermaid
sequenceDiagram
    actor U as Usuario
    participant T as API externa de Telegram
    participant P as Integración: TelegramBotPoller
    participant B as Service: TelegramSearchBotService
    participant S as Service: PieceSearchService
    participant R as Repository: PieceRepository
    participant DB as Base de datos H2

    U->>T: Enviar mensaje de búsqueda
    P->>T: getUpdates
    T-->>P: Mensajes pendientes
    P->>B: handleMessage(text)
    B->>B: Interpretar y validar filtros

    alt Solicitud de ayuda o filtros inválidos
        B-->>P: Instrucciones o mensaje de error
    else Búsqueda válida
        B->>S: searchPieces(name, description, date, tag)
        S->>R: Buscar piezas según los filtros
        R->>DB: Ejecutar consulta
        DB-->>R: Registros encontrados
        R-->>S: List de Piece
        S-->>B: List de PieceResponseDTO
        B->>B: Formatear resultados
        B-->>P: Mensaje de respuesta
    end

    P->>T: sendMessage(chatId, reply)
    T-->>U: Mostrar respuesta
```

## Consideraciones

- TelegramBotPoller consulta periódicamente la API de Telegram.
- TelegramSearchBotService interpreta los mensajes y filtros.
- PieceSearchService reutiliza la misma lógica de búsqueda
  utilizada por la API REST.
- Las respuestas respetan el límite de 4096 caracteres.
- El token se configura externamente y no se almacena
  en el repositorio.
- El bot forma parte de la misma aplicación monolítica.