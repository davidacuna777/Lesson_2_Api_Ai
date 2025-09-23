# API Spec

## /health (GET)
- 200 OK -> {"status":"ok"}

## /version (GET)
- 200 OK -> {"app":"Leccion2","version":"1.0.0"}

## /chat/generate (POST)
Request:
{
  "system": "optional",
  "prompt": "texto del usuario",
  "context": ["prev1","prev2"],
  "params": {"temperature": 0.7, "maxTokens": 512}
}
Response:
{
  "reply": "…",
  "usage": {"inputTokens": 0, "outputTokens": 0}
}

## /coach/analyze (POST)
Request:
{
  "conversation": [{"role":"user","content":"hola"}],
  "goal": "REJECT|UPSELL|MOTIVATE",
  "productHint": "opcional"
}
Response:
{
  "advice": "…",
  "rationale": "…",
  "suggestedPhrases": ["…","…","…"]
}

## /telegram/webhook (POST)
- body: update crudo de Telegram.
- Inserta chiste cada N mensajes por chatId.
