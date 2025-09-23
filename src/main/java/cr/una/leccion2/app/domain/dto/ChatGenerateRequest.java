package cr.una.leccion2.app.domain.dto;

import java.util.List;

public class ChatGenerateRequest {
    public String system;
    public String prompt;
    public java.util.List<String> context;
    public Params params;
    public static class Params {
        public Double temperature;
        public Integer maxTokens;
    }
}
