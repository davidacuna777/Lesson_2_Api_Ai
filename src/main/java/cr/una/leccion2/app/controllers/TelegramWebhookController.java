package cr.una.leccion2.app.controllers;

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
import cr.una.leccion2.app.services.ConversationContextService;
import cr.una.leccion2.app.usecases.HumorAgent;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private static final Logger log = LoggerFactory.getLogger(TelegramWebhookController.class);

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
        contextService.appendMessage(update.chatId, update.text);
        HumorAgent agent = useCaseFactory.humorAgent();
        String joke = agent.maybeTellJoke(update.chatId);
        if (joke != null && !joke.isBlank()) {
            telegramAdapter.sendMessage(update.chatId, joke);
            log.info("Sent contextual joke to chat {}", update.chatId);
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}