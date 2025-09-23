# Decisiones de diseño

- **Factory** como eje: `LLMClientFactory` selecciona `DeepSeekAdapter` o `MockLLMAdapter`.
- **Adapter**: aisla servicios externos (DeepSeek/Telegram) y formatos.
- **Strategy**: permite cambiar tonos de humor y objetivos de coaching sin tocar controladores.
- **Singleton**: `ConfigService` centraliza configuración vía variables de entorno.

Errores y observabilidad:
- Respuestas de error JSON con `errorCode`, `message`, `hint`.
- Timeouts moderados en Adapters.
