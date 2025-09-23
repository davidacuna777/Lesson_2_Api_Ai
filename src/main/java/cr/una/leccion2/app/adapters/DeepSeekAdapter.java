package cr.una.leccion2.app.adapters;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.qos.logback.classic.Logger;
import cr.una.leccion2.app.config.ConfigService;
import cr.una.leccion2.app.llm.GenParams;
import cr.una.leccion2.app.llm.LLMClient;
import cr.una.leccion2.app.llm.LLMResult;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Component
public class DeepSeekAdapter implements LLMClient {

      private static final Logger log = (Logger) LoggerFactory.getLogger(DeepSeekAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final ConfigService config;
    private final ObjectMapper mapper;
    private final OkHttpClient http;

    public DeepSeekAdapter(ConfigService config) {
           this(config, new OkHttpClient(), new ObjectMapper());
    }

    DeepSeekAdapter(ConfigService config, OkHttpClient http, ObjectMapper mapper) {
        this.config = config;
        this.http = http;
        this.mapper = mapper;
        
    }

    @Override
    public LLMResult generateReply(String system, String user, List<String> context, GenParams params) throws Exception {
        // Build OpenAI-compatible payload for DeepSeek chat completions
        ObjectNode root = mapper.createObjectNode();
        root.put("model", config.deepseekModel());
        ArrayNode messages = root.putArray("messages");
        if (system != null && !system.isBlank()) {
            ObjectNode sys = mapper.createObjectNode();
            sys.put("role", "system");
            sys.put("content", system);
            messages.add(sys);
        }
        if (context != null) {
            for (String s : context) {
                ObjectNode prev = mapper.createObjectNode();
                prev.put("role", "user");
                prev.put("content", s);
                messages.add(prev);
            }
        }
        ObjectNode usr = mapper.createObjectNode();
        usr.put("role", "user");
        usr.put("content", user);
        messages.add(usr);

        if (params != null) {
            if (params.temperature != null) root.put("temperature", params.temperature);
            if (params.maxTokens != null) root.put("max_tokens", params.maxTokens);
        }

        String baseUrl = config.deepseekBaseUrl();
        String url = baseUrl.endsWith("/") ? baseUrl + "v1/chat/completions" : baseUrl + "/v1/chat/completions";

        if (config.deepseekApiKey().isBlank()) {
            log.warn("DeepSeek API key no configurada, devolviendo mensaje local");
            return new LLMResult("[DeepSeek desactivado: configura DEEPSEEK_API_KEY] " + user, 0, 0);
        }

        RequestBody body = RequestBody.create(root.toString().getBytes(StandardCharsets.UTF_8), JSON);
        
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + config.deepseekApiKey())
                .addHeader("Content-Type", "application/json")
                .post(body).build();

        

        try (Response resp = http.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                String hint = resp.body() != null ? resp.body().string() : "";
                log.warn("DeepSeek HTTP {}: {}", resp.code(), hint);
                return new LLMResult("[DeepSeek error HTTP " + resp.code() + "]", 0, 0);
            }
            String json = resp.body() != null ? resp.body().string() : "{}";
            JsonNode node = mapper.readTree(json);
            String content = node.path("choices").path(0).path("message").path("content").asText("");
            int inTok = node.path("usage").path("prompt_tokens").asInt(0);
            int outTok = node.path("usage").path("completion_tokens").asInt(0);
            if (content.isBlank()) content = "[DeepSeek sin contenido]";
            return new LLMResult(content, inTok, outTok);
        }
    }
}
