# Lección 2 · Servidor Java 21 / Spring Boot 3

Aplicación sin BD ni autenticación que demuestra los patrones **Factory**, **Adapter**, **Strategy** y **Singleton**. Integra con un LLM (DeepSeek compatible OpenAI, con _mock_ por defecto) y con Telegram para inyectar humor contextual.

- **Factory:** selección de cliente LLM y ensamblaje de casos de uso.
- **Adapter:** DeepSeek, Telegram y cliente mock desacoplados de la lógica.
- **Strategy:** estilos de humor y tácticas de coaching de ventas.
- **Singleton:** `ConfigService` centraliza variables de entorno.

---

## Arquitectura

```
/config        # Configuración global + filtros HTTP
  /factory       # LLMClientFactory, UseCaseFactory
  /adapters      # DeepSeekAdapter, MockLLMAdapter, TelegramAdapter
  /strategies    # Humor (friendly/light/sarcastic) y ventas (Reject/Upsell/Motivate)
  /usecases      # ChatAgent, SalesCoach, HumorAgent
  /services      # ConversationContextService, MessageCounter
  /controllers   # REST: Health, Chat, Coach, Telegram webhook, handler de errores
  /domain        # DTOs y excepciones de dominio
/docs             # Especificaciones, decisiones y webhook de Telegram
```

La observabilidad mínima se garantiza con un `OncePerRequestFilter` que registra método, URL, status y latencia.

## Variables de entorno
Crea un archivo `.env` tomando como referencia `.env.example`.

| Variable | Descripción | Valor por defecto |
| --- | --- | --- |
| `PROVIDER` | Proveedor de LLM (`mock` o `deepseek`) | `mock` |
| `DEEPSEEK_API_KEY` | API key de DeepSeek (opcional) | vacío |
| `DEEPSEEK_BASE_URL` | URL base de DeepSeek | `https://api.deepseek.com` |
| `DEEPSEEK_MODEL` | Modelo a utilizar | `deepseek-chat` |
| `TELEGRAM_BOT_TOKEN` | Token del bot (si se deja vacío, modo mock) | vacío |
| `HTTP_PORT` | Puerto HTTP de la app | `8080` |
| `JOKE_EVERY_N_MESSAGES` | Frecuencia de chistes por chat en Telegram | `3` |
| `HUMOR_STYLE` | Estilo de humor (`friendly`, `light`, `sarcastic`) | `friendly` |
| `SALES_COACH_USE_LLM` | Refina el coaching con DeepSeek (requiere API key) | `false` |

## Ejecución
### Local (Java 21 + Maven)
```bash
mvn clean package
java -jar target/leccion2-servidor-factory-deepseek-telegram-1.0.0.jar
```
La aplicación respeta `HTTP_PORT` configurado.

### Docker
```bash
cp .env.example .env   # ajusta valores si es necesario
docker compose up -d
```
`docker-compose.yml` carga `.env`, publica `${HTTP_PORT}` en host y usa `restart: unless-stopped`.

## Endpoints y ejemplos cURL
> Reemplaza `localhost:8080` por el puerto configurado.

### Healthcheck
```bash
curl -s http://localhost:8080/health

```

### Versión
```bash
curl -s http://localhost:8080/version
```

### Chat (`POST /chat/generate`)
```bash
curl -s -X POST http://localhost:8080/chat/generate \
  -H 'Content-Type: application/json' \
  -d '{
        "system": "Eres un asistente positivo",
        "prompt": "¿Cuál es el estado del sprint?",
        "context": ["El demo fue bien"],
        "params": {"temperature": 0.4, "maxTokens": 120}
      }'
```
Respuesta:
```json
{
  "reply": "MOCK_REPLY → ¿Cuál es el estado del sprint? [CTX: El demo fue bien] [SYS: Eres un asistente positivo]",
  "usage": {"inputTokens": 0, "outputTokens": 123}
}
```

#### Coach de ventas (`POST /coach/analyze`)
```bash
curl -s -X POST http://localhost:8080/coach/analyze \
  -H 'Content-Type: application/json' \
  -d '{
        "goal": "UPSELL",
        "productHint": "Plan Premium",
        "conversation": [
          {"role": "user", "content": "¿Qué diferencia hay con el plan básico?"},
          {"role": "seller", "content": "El premium agrega soporte 24/7"}
        ]
      }'
```
Obtendrás `advice`, `rationale` y `suggestedPhrases` completos aun con PROVIDER=`mock`.

### Webhook de Telegram (`POST /telegram/webhook`)
```bash
curl -s -X POST http://localhost:8080/telegram/webhook \
  -H 'Content-Type: application/json' \
  -d '{"message":{"chat":{"id":"123"},"text":"Hicimos deploy del feature"}}'
```
Cada mensaje incrementa el contador del `chatId`. Cuando el total es múltiplo de `JOKE_EVERY_N_MESSAGES`, el bot envía un chiste contextual (o lo registra en logs si no hay token).


> Guía paso a paso para registrar el webhook en `docs/telegram-webhook.md`.

## Pruebas
```bash
mvn test
```
Incluye pruebas de controlador (`/coach/analyze`) y unidades para servicios/estrategias.

### Limitaciones actuales
- Estado en memoria: contador y contexto de chat se reinician al reiniciar el proceso.
- Sin persistencia ni autenticación.
- Estrategia de keywords simple (tokens frecuentes, sin lematización).
- El refinamiento con LLM solo se activa cuando `SALES_COACH_USE_LLM=true` y `PROVIDER=deepseek` con API key.

## Trabajo futuro sugerido
- **Streaming** de respuestas LLM vía Server-Sent Events.
- **Structured outputs** (JSON schema) para el coach y el chat.
- **Persistencia ligera** (Redis/Mongo) para conservar contexto cross-sesión.
- **Cache** o _buffer_ de conversación para optimizar llamadas a LLM.
- **Tests adicionales** para estrategias y webhook.

