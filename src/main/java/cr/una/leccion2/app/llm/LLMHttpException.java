package cr.una.leccion2.app.llm;

public class LLMHttpException extends Exception {
    private final int statusCode;
    private final String responseBody;
    private final String hint;

    public LLMHttpException(int statusCode, String message, String responseBody, String hint) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.hint = hint;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getHint() {
        return hint;
    }
}