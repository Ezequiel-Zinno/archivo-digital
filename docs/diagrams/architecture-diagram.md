# Diagramas de arquitectura — Archivo Digital

## 1. Diagrama de contexto

```mermaid
flowchart LR
    A[Administrador o cliente REST]
    U[Usuario de Telegram]
    T[API externa de Telegram]
    S[Sistema Archivo Digital]

    A -->|Solicitudes HTTP REST| S
    U <-->|Mensajes| T
    S <-->|HTTPS| T
```

El sistema permite registrar piezas, digitalizaciones,
administrar tags, realizar catalogaciones y buscar piezas.

Los usuarios también pueden realizar búsquedas mediante
el bot de Telegram.

## 2. Diagrama de arquitectura interna

```mermaid
flowchart TD
    Client[Cliente REST]
    Telegram[API externa de Telegram]

    subgraph App["Aplicación monolítica Spring Boot"]
        subgraph Presentation["Capa de presentación"]
            Controllers[Controllers REST]
        end

        subgraph Integration["Integración externa"]
            Poller[TelegramBotPoller]
        end

        subgraph Business["Capa de negocio"]
            Services[Servicios de piezas, digitalizaciones, tags y catalogación]
            BotService[TelegramSearchBotService]
            SearchService[PieceSearchService]
            Model[Entidades del dominio]
        end

        subgraph Persistence["Capa de acceso a datos"]
            Repositories[Repositories JPA]
        end

        Controllers --> Services
        Controllers --> SearchService
        Poller --> BotService
        BotService --> SearchService
        Services --> Model
        Services --> Repositories
        SearchService --> Repositories
        Repositories -.-> Model
    end

    DB[(Base de datos H2)]

    Client --> Controllers
    Poller <-->|HTTPS| Telegram
    Repositories --> DB
```

## 3. Decisiones de arquitectura

- Se utiliza una arquitectura monolítica con una única
  aplicación Spring Boot y una única base de datos.
- La aplicación se organiza en capas, separando presentación,
  negocio y acceso a datos.
- Los controllers reciben solicitudes HTTP y delegan
  las operaciones a los servicios.
- Los servicios coordinan los casos de uso y aplican
  las reglas correspondientes junto con el modelo.
- Los repositorios gestionan la persistencia mediante JPA.
- Los DTOs permiten intercambiar datos sin exponer
  directamente las entidades del dominio.
- El bot de Telegram forma parte del mismo monolito.
- TelegramSearchBotService reutiliza PieceSearchService
  sin realizar solicitudes HTTP a la propia API REST.
- H2 funciona en memoria en el perfil local.
- La API de Telegram es una dependencia externa,
  no un componente independiente desarrollado por el equipo.