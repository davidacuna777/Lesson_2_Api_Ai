package cr.una.leccion2.app;

import cr.una.leccion2.app.adapters.DeepSeekAdapter;
import cr.una.leccion2.app.adapters.MockLLMAdapter;
import cr.una.leccion2.app.config.ConfigService;
import cr.una.leccion2.app.factory.LLMClientFactory;
import cr.una.leccion2.app.llm.LLMClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryTest {

    static class TestConfig extends ConfigService {
        private final String provider;
        TestConfig(String provider){ this.provider=provider; }
        @Override public String provider() { return provider; }
    }

    @Test
    void choosesMockByDefault() {
        LLMClientFactory f = new LLMClientFactory(new TestConfig("mock"), new DeepSeekAdapter(new TestConfig("mock")), new MockLLMAdapter());
        LLMClient c = f.getClient();
        Assertions.assertThat(c).isInstanceOf(MockLLMAdapter.class);
    }

    @Test
    void choosesDeepSeek() {
        LLMClientFactory f = new LLMClientFactory(new TestConfig("deepseek"), new DeepSeekAdapter(new TestConfig("deepseek")), new MockLLMAdapter());
        LLMClient c = f.getClient();
        Assertions.assertThat(c).isInstanceOf(DeepSeekAdapter.class);
    }
}
