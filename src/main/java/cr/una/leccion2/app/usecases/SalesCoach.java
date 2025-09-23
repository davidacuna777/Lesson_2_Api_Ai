package cr.una.leccion2.app.usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import cr.una.leccion2.app.llm.GenParams;
import cr.una.leccion2.app.llm.LLMClient;
import cr.una.leccion2.app.llm.LLMResult;
import cr.una.leccion2.app.strategies.sales.SalesStrategy;

public class SalesCoach {

    public enum Goal { REJECT, UPSELL, MOTIVATE }

    public static class Advice {
        public String advice;
        public String rationale;
        public String[] suggestedPhrases;
        public int inputTokens;
        public int outputTokens;
    }

    private final LLMClient llm;
    private final SalesStrategy rejectStrategy;
    private final SalesStrategy upsellStrategy;
    private final SalesStrategy motivateStrategy;
    private final boolean refineWithLlm;

    public SalesCoach(LLMClient llm,
                      SalesStrategy rejectStrategy,
                      SalesStrategy upsellStrategy,
                      SalesStrategy motivateStrategy,
                      boolean refineWithLlm) {
        this.llm = llm;
        this.rejectStrategy = rejectStrategy;
        this.upsellStrategy = upsellStrategy;
        this.motivateStrategy = motivateStrategy;
        this.refineWithLlm = refineWithLlm;
    }

    public Advice advise(List<String> conversation, Goal goal, String productHint) throws Exception {
        SalesStrategy strategy = selectStrategy(goal);
        Advice base = new Advice();
        List<String> safeConversation = conversation == null ? List.of() : conversation;
        base.advice = strategy.advice(safeConversation, productHint);
        base.rationale = strategy.rationale(safeConversation, productHint);
        base.suggestedPhrases = strategy.suggestedPhrases(safeConversation, productHint);

        if (!refineWithLlm) {
            return base;
        }

        LLMResult refinement = llm.generateReply(
                "Eres un coach de ventas sénior que da retroalimentación clara",
                buildPrompt(goal, productHint, safeConversation, base),
                safeConversation,
                defaultParams()
        );

        Advice merged = mergeAdvice(base, refinement);
        merged.inputTokens = refinement != null ? refinement.inputTokens : 0;
        merged.outputTokens = refinement != null ? refinement.outputTokens : 0;
        return merged;
    }

    private SalesStrategy selectStrategy(Goal goal) {
        return switch (goal) {
            case REJECT -> rejectStrategy;
            case UPSELL -> upsellStrategy;
            case MOTIVATE -> motivateStrategy;
        };
    }

    private GenParams defaultParams() {
        GenParams params = new GenParams();
        params.temperature = 0.3;
        params.maxTokens = 250;
        return params;
    }

    private String buildPrompt(Goal goal,
                               String productHint,
                               List<String> conversation,
                               Advice base) {
        StringBuilder sb = new StringBuilder();
        sb.append("Objetivo: ").append(goal.name()).append('\n');
        if (productHint != null && !productHint.isBlank()) {
            sb.append("Producto clave: ").append(productHint).append('\n');
        }
        if (!conversation.isEmpty()) {
            sb.append("Resumen de la conversación:\n");
            for (String line : conversation) {
                sb.append("- ").append(line).append('\n');
            }
        }
        sb.append("Consejo base: ").append(safe(base.advice)).append('\n');
        sb.append("Racional base: ").append(safe(base.rationale)).append('\n');
        if (base.suggestedPhrases != null && base.suggestedPhrases.length > 0) {
            sb.append("Frases base: ");
            StringJoiner joiner = new StringJoiner(" | ");
            for (String phrase : base.suggestedPhrases) {
                joiner.add(safe(phrase));
            }
            sb.append(joiner);
        }
        sb.append("\nGenera respuesta usando el formato:\n");
        sb.append("ADVICE: ...\nRATIONALE: ...\nPHRASES: frase 1 | frase 2 | frase 3");
        return sb.toString();
    }

    private Advice mergeAdvice(Advice base, LLMResult refinement) {
        if (refinement == null || refinement.text == null || refinement.text.isBlank()) {
            return base;
        }
        String text = refinement.text.trim();
        String advice = extractSection(text, "ADVICE:");
        String rationale = extractSection(text, "RATIONALE:");
        String phrasesRaw = extractSection(text, "PHRASES:");

        boolean hasContent = false;
        Advice merged = new Advice();
        merged.advice = (advice != null && !advice.isBlank()) ? advice.trim() : base.advice;
        if (advice != null && !advice.isBlank()) {
            hasContent = true;
        }
        merged.rationale = (rationale != null && !rationale.isBlank()) ? rationale.trim() : base.rationale;
        if (rationale != null && !rationale.isBlank()) {
            hasContent = true;
        }
        if (phrasesRaw != null && !phrasesRaw.isBlank()) {
            String[] phrases = parsePhrases(phrasesRaw);
            if (phrases.length > 0) {
                merged.suggestedPhrases = phrases;
                hasContent = true;
            } else {
                merged.suggestedPhrases = base.suggestedPhrases;
            }
        } else {
            merged.suggestedPhrases = base.suggestedPhrases;
        }

        if (!hasContent) {
            return base;
        }
        return merged;
    }

    private String[] parsePhrases(String raw) {
        String[] pieces = raw.split("\\|");
        List<String> phrases = new ArrayList<>();
        for (String piece : pieces) {
            String trimmed = piece.trim();
            if (!trimmed.isEmpty()) {
                phrases.add(trimmed);
            }
        }
        return phrases.toArray(String[]::new);
    }

    private String extractSection(String text, String marker) {
        int idx = text.toUpperCase().indexOf(marker);
        if (idx < 0) {
            return null;
        }
        int start = idx + marker.length();
        int end = text.indexOf('\n', start);
        String section = end >= 0 ? text.substring(start, end) : text.substring(start);
        return section.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public static Goal parseGoal(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("INVALID_GOAL");
        }
        try {
            return Goal.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("INVALID_GOAL");
        }
    }
}