package cr.una.leccion2.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.una.leccion2.app.domain.dto.ChatGenerateRequest;
import cr.una.leccion2.app.domain.dto.ChatGenerateResponse;
import cr.una.leccion2.app.factory.UseCaseFactory;
import cr.una.leccion2.app.llm.GenParams;
import cr.una.leccion2.app.llm.LLMResult;
import cr.una.leccion2.app.usecases.ChatAgent;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final UseCaseFactory factory;

    public ChatController(UseCaseFactory factory) {
        this.factory = factory;
    }

    @PostMapping("/generate")
    public ResponseEntity<ChatGenerateResponse> generate(@RequestBody ChatGenerateRequest request) throws Exception {
        if (request == null || request.prompt == null || request.prompt.isBlank()) {
            throw new IllegalArgumentException("PROMPT_REQUIRED");
        }
        ChatAgent agent = factory.chatAgent();
        GenParams params = mapParams(request);
        LLMResult result = agent.reply(request.system, request.prompt, request.context, params);

        ChatGenerateResponse response = new ChatGenerateResponse();
        response.reply = result.text;
        response.usage = new ChatGenerateResponse.Usage();
        response.usage.inputTokens = result.inputTokens;
        response.usage.outputTokens = result.outputTokens;
        return ResponseEntity.ok(response);
    }

    private GenParams mapParams(ChatGenerateRequest request) {
        if (request.params == null) {
            return null;
        }
        GenParams params = new GenParams();
        params.temperature = request.params.temperature;
        params.maxTokens = request.params.maxTokens;
        return params;
    }
}