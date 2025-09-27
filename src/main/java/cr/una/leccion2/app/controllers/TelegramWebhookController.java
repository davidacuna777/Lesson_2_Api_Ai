package cr.una.leccion2.app.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.una.leccion2.app.adapters.TelegramAdapter;
import cr.una.leccion2.app.factory.UseCaseFactory;
import cr.una.leccion2.app.llm.LLMResult;
import cr.una.leccion2.app.services.ConversationContextService;
import cr.una.leccion2.app.usecases.ChatAgent;
import cr.una.leccion2.app.usecases.HumorAgent;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private static final Logger log = LoggerFactory.getLogger(TelegramWebhookController.class);
       private static final String TELEGRAM_SYSTEM_PROMPT =
            "Eres un asistente cercano y empático. Responde en español usando frases breves y claras.";
    private static final String FALLBACK_REPLY =
            "Ups, no pude responder ahora mismo. Inténtalo de nuevo en unos segundos.";
    private static final int CONTEXT_LIMIT = 5;


    private final TelegramAdapter telegramAdapter;
    private final UseCaseFactory useCaseFactory;
    private final ConversationContextService contextService;

    public TelegramWebhookController(TelegramAdapter telegramAdapter,
                                     UseCaseFactory useCaseFactory,
                                     ConversationContextService contextService) {
        this.telegramAdapter = telegramAdapter;
        this.useCaseFactory = useCaseFactory;
        this.contextService = contextService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> webhook(@RequestBody String payload) throws Exception {
        TelegramAdapter.ParsedUpdate update = telegramAdapter.parse(payload);
        if (update.chatId == null) {
            return ResponseEntity.ok(Map.of("status", "ignored"));
        }
      String incoming = update.text == null ? "" : update.text.trim();
        if (incoming.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "ignored"));
        }

        List<String> previous = contextService.lastMessages(update.chatId, CONTEXT_LIMIT);
        contextService.appendMessage(update.chatId, incoming);

        ChatAgent chatAgent = useCaseFactory.chatAgent();
        String replyText;
        try {
            LLMResult result = chatAgent.reply(TELEGRAM_SYSTEM_PROMPT, incoming, previous, null);
            replyText = (result != null && result.text != null && !result.text.isBlank())
                    ? result.text
                    : FALLBACK_REPLY;
        } catch (Exception ex) {
            log.warn("Error generating Telegram reply: {}", ex.toString());
            replyText = FALLBACK_REPLY;
        }

        contextService.appendMessage(update.chatId, replyText);
        telegramAdapter.sendMessage(update.chatId, replyText);

        HumorAgent agent = useCaseFactory.humorAgent();
        String joke = agent.maybeTellJoke(update.chatId);
        if (joke != null && !joke.isBlank()) {
            telegramAdapter.sendMessage(update.chatId, joke);
            log.info("Sent contextual joke to chat {}", update.chatId);
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}