package cr.una.leccion2.app.usecases;

import cr.una.leccion2.app.services.ConversationContextService;
import cr.una.leccion2.app.services.MessageCounter;
import cr.una.leccion2.app.strategies.humor.JokeStrategy;

import java.util.List;

public class HumorAgent {

    private final JokeStrategy strategy;
    private final MessageCounter counter;
    private final ConversationContextService contextService;
    private final int frequency;

    public HumorAgent(JokeStrategy strategy,
                      MessageCounter counter,
                      ConversationContextService contextService,
                      int frequency) {
        this.strategy = strategy;
        this.counter = counter;
        this.contextService = contextService;
        this.frequency = Math.max(frequency, 1);
    }

    public String maybeTellJoke(String chatId) {
        if (chatId == null || chatId.isBlank()) {
            return null;
        }
        int total = counter.increment(chatId);
        if (total % frequency != 0) {
            return null;
        }
        List<String> keywords = contextService.extractKeywords(chatId, 3);
        return strategy.makeJoke(keywords);
    }
}