# Telegram Webhook (guía rápida)

1) Crea bot con @BotFather → guarda `TELEGRAM_BOT_TOKEN` en `.env`.
2) Expón públicamente `POST /telegram/webhook` (ngrok u otro túnel).
3) setWebhook:
   https://api.telegram.org/bot<TELEGRAM_BOT_TOKEN>/setWebhook?url=<PUBLIC_URL>/telegram/webhook
4) Envía mensajes al bot y verifica que cada N mensajes el servidor responda con un chiste contextual.
