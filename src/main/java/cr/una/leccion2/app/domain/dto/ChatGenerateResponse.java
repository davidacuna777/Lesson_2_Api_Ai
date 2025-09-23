package cr.una.leccion2.app.domain.dto;

public class ChatGenerateResponse {
    public String reply;
    public Usage usage;
    public static class Usage {
        public int inputTokens;
        public int outputTokens;
    }
}
