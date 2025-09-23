package cr.una.leccion2.app.usecases;

import cr.una.leccion2.app.strategies.sales.SalesStrategy;
import cr.una.leccion2.app.llm.*;

import java.util.ArrayList;
import java.util.List;

public class SalesCoach {

    private final LLMClient llm;
    private final SalesStrategy reject;
    private final SalesStrategy upsell;
    private final SalesStrategy motivate;

    public SalesCoach(LLMClient llm, SalesStrategy reject, SalesStrategy upsell, SalesStrategy motivate) {
        this.llm = llm;
        this.reject = reject;
        this.upsell = upsell;
        this.motivate = motivate;
    }

    public static enum Goal { REJECT, UPSELL, MOTIVATE }

    public Advice advise(List<String> conversation, Goal goal, String productHint) throws Exception {
        SalesStrategy s = switch (goal) {
            case REJECT -> reject;
            case UPSELL -> upsell;
            case MOTIVATE -> motivate;
        };
        String advice = s.advice(conversation, productHint);
        String rationale = s.rationale(conversation, productHint);
        String[] phrases = s.suggestedPhrases(conversation, productHint);

        // Optional: refine with LLM (kept simple for cost-free default)
        return new Advice(advice, rationale, phrases);
    }

    public static class Advice {
        public final String advice;
        public final String rationale;
        public final String[] suggestedPhrases;
        public Advice(String a, String r, String[] p) { advice=a; rationale=r; suggestedPhrases=p; }
    }
}
