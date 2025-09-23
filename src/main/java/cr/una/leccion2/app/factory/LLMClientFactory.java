package cr.una.leccion2.app.factory;

import cr.una.leccion2.app.adapters.DeepSeekAdapter;
import cr.una.leccion2.app.adapters.MockLLMAdapter;
import cr.una.leccion2.app.config.ConfigService;
import cr.una.leccion2.app.llm.LLMClient;
import org.springframework.stereotype.Component;

@Component
public class LLMClientFactory {

    private final ConfigService config;
    private final DeepSeekAdapter deepSeek;
    private final MockLLMAdapter mock;

    public LLMClientFactory(ConfigService config, DeepSeekAdapter deepSeek, MockLLMAdapter mock) {
        this.config = config;
        this.deepSeek = deepSeek;
        this.mock = mock;
    }

    public LLMClient getClient() {
        String p = config.provider().toLowerCase();
        return switch (p) {
            case "deepseek" -> deepSeek;
            default -> mock;
        };
    }
}
