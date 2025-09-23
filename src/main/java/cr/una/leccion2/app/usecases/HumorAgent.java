package cr.una.leccion2.app.usecases;

import cr.una.leccion2.app.services.ConversationContextService;
import cr.una.leccion2.app.services.MessageCounter;
import cr.una.leccion2.app.strategies.humor.JokeStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

public class HumorAgent {

    private final JokeStrategy strategy;
    private final MessageCounter counter;
    private final int n;

    public HumorAgent(JokeStrategy strategy, MessageCounter counter, int jokeEveryN) {
        this.strategy = strategy;
        this.counter = counter;
        this.n = Math.max(2, jokeEveryN);
    }

    public String maybeInject(String chatId, List<String> history) {
        int c = counter.increment(chatId);
        if (c % n == 0) {
            return strategy.makeJoke(history);
        }
        return null;
    }
}
