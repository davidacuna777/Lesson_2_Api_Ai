package cr.una.leccion2.app.strategies.sales;

import java.util.List;

public interface SalesStrategy {
    String advice(List<String> conversation, String productHint);
    String rationale(List<String> conversation, String productHint);
    String[] suggestedPhrases(List<String> conversation, String productHint);
}
