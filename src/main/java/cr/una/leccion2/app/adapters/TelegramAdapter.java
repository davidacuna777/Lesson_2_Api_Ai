package cr.una.leccion2.app.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.una.leccion2.app.config.ConfigService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelegramAdapter {
    private static final Logger log = LoggerFactory.getLogger(TelegramAdapter.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient http = new OkHttpClient();
    private final ConfigService config;

    public TelegramAdapter(ConfigService config) {
        this.config = config;
    }

    public static class ParsedUpdate {
        public String chatId;
        public String text;
    }

    public ParsedUpdate parse(String updateJson) throws Exception {
        JsonNode node = mapper.readTree(updateJson);
        ParsedUpdate p = new ParsedUpdate();
        JsonNode msg = node.path("message");
        p.chatId = msg.path("chat").path("id").asText(null);
        p.text = msg.path("text").asText("");
        return p;
    }

    public void sendMessage(String chatId, String text) {
        String token = config.telegramToken();
        if (token == null || token.isBlank()) {
            log.info("[Telegram MOCK] to {} → {}", chatId, text);
            return;
        }
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";
        RequestBody body = new FormBody.Builder()
                .add("chat_id", chatId)
                .add("text", text)
                .build();
        Request req = new Request.Builder().url(url).post(body).build();
        try (Response resp = http.newCall(req).execute()) {
            log.info("Telegram sendMessage status: {}", resp.code());
        } catch (Exception ex) {
            log.warn("Telegram sendMessage error: {}", ex.toString());
        }
    }
}
