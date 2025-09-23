package cr.una.leccion2.app.adapters;

import cr.una.leccion2.app.llm.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MockLLMAdapter implements LLMClient {
    @Override
    public LLMResult generateReply(String system, String user, List<String> context, GenParams params) {
        String ctx = (context == null || context.isEmpty()) ? "" :
                " [CTX: " + context.stream().limit(3).collect(Collectors.joining(" | ")) + "]";
        String sys = (system == null || system.isBlank()) ? "" : " [SYS: " + system + "]";
        String reply = "MOCK_REPLY → " + user + ctx + sys;
        return new LLMResult(reply, 0, reply.length());
    }
}
