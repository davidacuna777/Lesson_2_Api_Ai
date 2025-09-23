package cr.una.leccion2.app.domain.dto;

import java.util.List;

public class CoachAnalyzeRequest {
    public java.util.List<MessageDto> conversation;
    public String goal; // REJECT|UPSELL|MOTIVATE
    public String productHint;
}
