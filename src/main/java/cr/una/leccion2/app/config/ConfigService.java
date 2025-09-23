package cr.una.leccion2.app.config;

import org.springframework.stereotype.Component;

@Component
public class ConfigService {

    public String provider() { return env("PROVIDER", "mock"); }

    public String deepseekApiKey() { return env("DEEPSEEK_API_KEY", ""); }
    public String deepseekBaseUrl() { return env("DEEPSEEK_BASE_URL", "https://api.deepseek.com"); }
    public String deepseekModel() { return env("DEEPSEEK_MODEL", "deepseek-chat"); }

    public String telegramToken() { return env("TELEGRAM_BOT_TOKEN", ""); }

    public int httpPort() { return Integer.parseInt(env("HTTP_PORT", "8080")); }
    public int jokeEveryN() { return Integer.parseInt(env("JOKE_EVERY_N_MESSAGES", "3")); }
    public String humorStyle() { return env("HUMOR_STYLE", "friendly"); }
    public boolean salesCoachUseLlm() { return Boolean.parseBoolean(env("SALES_COACH_USE_LLM", "false")); }

    private String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}
