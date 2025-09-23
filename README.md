# Lección 2 · Servidor (Java 21 / Spring Boot 3)

**Patrones:** Factory (eje), Adapter, Strategy, Singleton · **Sin BD, sin auth** · **Dockerizado**  
**Integraciones:** DeepSeek (compatible OpenAI) y Telegram (webhook, chiste cada N mensajes).

> Proyecto generado el 2025-09-23.
> Por defecto usa `PROVIDER=mock` para ejecutar sin red ni costos.

---

## 1) Arquitectura

```
/app
  /config        # Singleton: ConfigService
  /factory       # Factory: LLMClientFactory, UseCaseFactory
  /adapters      # Adapter: DeepSeekAdapter, MockLLMAdapter, TelegramAdapter
  /strategies    # Strategy: humor/*, sales/*
  /usecases      # Orquestadores: ChatAgent, SalesCoach, HumorAgent
  /services      # Soporte: ConversationContextService, MessageCounter
  /controllers   # REST: Health, Chat, Coach, Telegram webhook
  /domain        # DTOs, enums, modelos de dominio
/docs             # Especificación, decisiones y enunciado
```

- **Factory**: crea el cliente LLM (`deepseek|mock`) y los casos de uso.
- **Adapter**: encapsula servicios externos (DeepSeek/Telegram).
- **Strategy**: cambia lógica (humor y coaching de ventas).
- **Singleton**: configuración vía variables de entorno.

## 2) Variables de entorno
Ver `.env.example`. Copia a `.env` y ajusta valores. Por defecto:
```
PROVIDER=mock
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-chat
HTTP_PORT=8080
JOKE_EVERY_N_MESSAGES=3
HUMOR_STYLE=friendly
```

## 3) Endpoints
- `GET /health` → `{"status":"ok"}`  
- `GET /version` → `{"app":"Leccion2", "version":"1.0.0"}`  
- `POST /chat/generate` → Chat libre (acepta `system`, `prompt`, `context`, `params`)  
- `POST /coach/analyze` → Coach de ventas (`goal`: REJECT | UPSELL | MOTIVATE)  
- `POST /telegram/webhook` → Webhook de Telegram; cada N mensajes inyecta chiste contextual

## 4) Ejecución (local o Docker)
- **Local** (requiere Java 21 y Maven):  
  - Empaquetar: `mvn clean package`  
  - Ejecutar: `java -jar target/leccion2-servidor-factory-deepseek-telegram-1.0.0.jar`
- **Docker**:  
  - `docker compose up -d` (usa `.env` y expone `HTTP_PORT`)

> Para activar DeepSeek, coloca tu key en `.env` y cambia `PROVIDER=deepseek`.

## 5) Pruebas
- `mvn test` ejecuta pruebas unitarias básicas de Factory, Strategies y MessageCounter.

## 6) Notas
- El Adapter de Telegram registra envíos; si defines `TELEGRAM_BOT_TOKEN`, intentará usar `sendMessage` del API oficial.  
- No se almacena estado persistente; el contador vive en memoria.

## 7) Créditos/Equipo
- Estructura preparada para trabajo en 2–3 horas y subir a GitHub con screenshots en `docs/screenshots/`.

---

### Enunciado
Se incluye el **enunciado** íntegro en `docs/enunciado.md`.
