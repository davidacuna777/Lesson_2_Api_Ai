package cr.una.leccion2.app.usecases;

import cr.una.leccion2.app.llm.*;
import cr.una.leccion2.app.services.ConversationContextService;

import java.util.List;

public class ChatAgent {

    private final LLMClient llm;
    private final ConversationContextService ctx;

    public ChatAgent(LLMClient llm, ConversationContextService ctx) {
        this.llm = llm;
        this.ctx = ctx;
    }

    public LLMResult reply(String system, String prompt, List<String> context, GenParams params) throws Exception {
        List<String> slim = ctx.lastMessages(context, 5);
        return llm.generateReply(system, prompt, slim, params);
    }
}
