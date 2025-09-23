package cr.una.leccion2.app.controllers;

import cr.una.leccion2.app.domain.dto.ChatGenerateRequest;
import cr.una.leccion2.app.domain.dto.ChatGenerateResponse;
import cr.una.leccion2.app.factory.UseCaseFactory;
import cr.una.leccion2.app.llm.GenParams;
import cr.una.leccion2.app.llm.LLMResult;
import cr.una.leccion2.app.usecases.ChatAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final UseCaseFactory factory;
    public ChatController(UseCaseFactory factory) { this.factory = factory; }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody ChatGenerateRequest req) {
        try {
            ChatAgent agent = factory.chatAgent();
            GenParams p = new GenParams();
            if (req.params != null) {
                p.temperature = req.params.temperature;
                p.maxTokens = req.params.maxTokens;
            }
            List<String> ctx = req.context == null ? new ArrayList<>() : req.context;
            LLMResult r = agent.reply(req.system, req.prompt, ctx, p);
            ChatGenerateResponse out = new ChatGenerateResponse();
            out.reply = r.text;
            out.usage = new ChatGenerateResponse.Usage();
            out.usage.inputTokens = r.inputTokens;
            out.usage.outputTokens = r.outputTokens;
            return ResponseEntity.ok(out);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(
                java.util.Map.of("errorCode","CHAT_ERROR","message",ex.getMessage(),"hint","Revisa Provider y API key")
            );
        }
    }
}
