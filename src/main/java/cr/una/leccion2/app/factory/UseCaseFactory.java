package cr.una.leccion2.app.factory;

import cr.una.leccion2.app.config.ConfigService;
import cr.una.leccion2.app.llm.LLMClient;
import cr.una.leccion2.app.services.ConversationContextService;
import cr.una.leccion2.app.services.MessageCounter;
import cr.una.leccion2.app.strategies.humor.*;
import cr.una.leccion2.app.strategies.sales.*;
import cr.una.leccion2.app.usecases.ChatAgent;
import cr.una.leccion2.app.usecases.HumorAgent;
import cr.una.leccion2.app.usecases.SalesCoach;
import org.springframework.stereotype.Component;

@Component
public class UseCaseFactory {

    private final LLMClientFactory llmFactory;
    private final ConfigService config;
    private final ConversationContextService ctxService;
    private final MessageCounter msgCounter;

    public UseCaseFactory(LLMClientFactory llmFactory, ConfigService config,
                          ConversationContextService ctxService, MessageCounter msgCounter) {
        this.llmFactory = llmFactory;
        this.config = config;
        this.ctxService = ctxService;
        this.msgCounter = msgCounter;
    }

    public ChatAgent chatAgent() {
        LLMClient llm = llmFactory.getClient();
        return new ChatAgent(llm, ctxService);
    }

    public SalesCoach salesCoach() {
        LLMClient llm = llmFactory.getClient();
        SalesStrategy reject = new RejectReturnStrategy();
        SalesStrategy upsell = new UpsellStrategy();
        SalesStrategy motivate = new MotivatePurchaseStrategy();
        return new SalesCoach(llm, reject, upsell, motivate);
    }

    public HumorAgent humorAgent() {
        JokeStrategy strategy = switch (config.humorStyle().toLowerCase()) {
            case "sarcastic" -> new SarcasticJokeStrategy();
            case "light" -> new LightJokeStrategy();
            default -> new FriendlyJokeStrategy();
        };
        return new HumorAgent(strategy, msgCounter, config.jokeEveryN());
    }
}
