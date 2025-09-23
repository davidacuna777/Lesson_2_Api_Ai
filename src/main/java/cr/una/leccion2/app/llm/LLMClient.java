package cr.una.leccion2.app.llm;

import java.util.List;

public interface LLMClient {
    LLMResult generateReply(String system, String user, List<String> context, GenParams params) throws Exception;
}
