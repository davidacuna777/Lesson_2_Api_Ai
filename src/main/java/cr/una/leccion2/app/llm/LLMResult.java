package cr.una.leccion2.app.llm;

public class LLMResult {
    public String text;
    public int inputTokens;
    public int outputTokens;

    public LLMResult(String text, int inTok, int outTok) {
        this.text = text;
        this.inputTokens = inTok;
        this.outputTokens = outTok;
    }
}
