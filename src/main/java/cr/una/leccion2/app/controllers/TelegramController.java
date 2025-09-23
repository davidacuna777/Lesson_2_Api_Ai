package cr.una.leccion2.app.controllers;

import cr.una.leccion2.app.adapters.TelegramAdapter;
import cr.una.leccion2.app.factory.UseCaseFactory;
import cr.una.leccion2.app.services.ConversationContextService;
import cr.una.leccion2.app.usecases.HumorAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/telegram")
public class TelegramController {

    private final TelegramAdapter telegram;
    private final UseCaseFactory factory;
    private final ConversationContextService ctxService;

    public TelegramController(TelegramAdapter telegram, UseCaseFactory factory, ConversationContextService ctxService) {
        this.telegram = telegram;
        this.factory = factory;
        this.ctxService = ctxService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody String updateJson) {
        try {
            TelegramAdapter.ParsedUpdate p = telegram.parse(updateJson);
            HumorAgent humor = factory.humorAgent();
            List<String> history = new ArrayList<>();
            history.add(p.text);
            var kws = ctxService.extractKeywords(history, 3);
            String maybe = humor.maybeInject(p.chatId, kws);
            if (maybe != null) {
                telegram.sendMessage(p.chatId, maybe);
            }
            return ResponseEntity.ok(java.util.Map.of("ok", true, "injected", maybe != null));
        } catch (Exception ex) {
            return ResponseEntity.status(200).body(java.util.Map.of("ok", true, "injected", false, "hint", "update sin texto"));
        }
    }
}
