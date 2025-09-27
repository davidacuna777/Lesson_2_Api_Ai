package cr.una.leccion2.app.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConfigService {


    private static final Logger log = LoggerFactory.getLogger(ConfigService.class);
    private static final Map<String, String> DOT_ENV = loadDotEnv();

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
        if (v == null || v.isBlank()) {
            v = DOT_ENV.get(key);
        }
        return (v == null || v.isBlank()) ? def : v;
    }

    private static Map<String, String> loadDotEnv() {
        Path path = Path.of(".env");
        if (!Files.exists(path)) {
            return Collections.emptyMap();
        }
        Map<String, String> values = new HashMap<>();
        try {
            for (String line : Files.readAllLines(path)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).trim();
                String value = trimmed.substring(eq + 1).trim();
                if (!key.isEmpty()) {
                    values.put(key, value);
                }
            }
        } catch (IOException ex) {
            log.warn("Unable to read .env file: {}", ex.toString());
        }
        return Collections.unmodifiableMap(values);
    }
}
