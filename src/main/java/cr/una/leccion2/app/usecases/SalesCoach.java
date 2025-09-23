package cr.una.leccion2.app.usecases;

import cr.una.leccion2.app.llm.GenParams;
import cr.una.leccion2.app.llm.LLMClient;
import cr.una.leccion2.app.llm.LLMHttpException;
import cr.una.leccion2.app.llm.LLMResult;

import cr.una.leccion2.app.strategies.sales.SalesStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SalesCoach {

     private static final Logger log = LoggerFactory.getLogger(SalesCoach.class);
    
    
    private final LLMClient llm;
    private final SalesStrategy reject;
    private final SalesStrategy upsell;
    private final SalesStrategy motivate;
    private final boolean refineWithLlm;

     public SalesCoach(LLMClient llm, SalesStrategy reject, SalesStrategy upsell, SalesStrategy motivate, boolean refineWithLlm) {
        this.llm = llm;
        this.reject = reject;
        this.upsell = upsell;
        this.motivate = motivate;
        this.refineWithLlm = refineWithLlm;
    }

    public static enum Goal { REJECT, UPSELL, MOTIVATE }

    public Advice advise(List<String> conversation, Goal goal, String productHint) throws Exception {
           List<String> safeConversation = conversation == null ? List.of() : new ArrayList<>(conversation);
        SalesStrategy s = switch (goal) {
            case REJECT -> reject;
            case UPSELL -> upsell;
            case MOTIVATE -> motivate;
        };
        String advice = safeText(s.advice(safeConversation, productHint),
                "Recapitula necesidades y responde con empatía.");
        String rationale = safeText(s.rationale(safeConversation, productHint),
                "Mantiene la relación con el cliente y orienta hacia el objetivo indicado.");
        String[] phrases = ensurePhrases(s.suggestedPhrases(safeConversation, productHint));

         Advice base = new Advice(advice, rationale, phrases);
        if (!refineWithLlm) {
            return base;
        }
        Advice refined = tryRefineWithLlm(safeConversation, goal, productHint, base);
        return refined != null ? refined : base;
    }

    public static class Advice {
        public final String advice;
        public final String rationale;
        public final String[] suggestedPhrases;
        public Advice(String a, String r, String[] p) { advice=a; rationale=r; suggestedPhrases=p; }
    }

    private String safeText(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }

    private String[] ensurePhrases(String[] phrases) {
        if (phrases == null || phrases.length == 0) {
            return new String[]{
                    "Estoy para ayudarte, contame un poco más y buscamos la mejor opción.",
                    "Podemos enfocarnos en el beneficio principal que te interesa.",
                    "¿Te parece si avanzamos con el siguiente paso?"
            };
        }
        List<String> filtered = new ArrayList<>();
        for (String phrase : phrases) {
            if (phrase != null && !phrase.isBlank()) {
                filtered.add(phrase.trim());
            }
        }
        if (filtered.isEmpty()) {
            return new String[]{
                    "Estoy para ayudarte, contame un poco más y buscamos la mejor opción.",
                    "Podemos enfocarnos en el beneficio principal que te interesa.",
                    "¿Te parece si avanzamos con el siguiente paso?"
            };
        }
        return filtered.toArray(String[]::new);
    }

    private Advice tryRefineWithLlm(List<String> conversation, Goal goal, String productHint, Advice base) throws Exception {
        if (!refineWithLlm) {
            return null;
        }
        List<String> limitedConversation = limitConversation(conversation, 8);
        String system = "Eres un coach de ventas senior. Devuelve EXACTAMENTE este formato: " +
                "ADVICE: ...\\nRATIONALE: ...\\nPHRASES: frase1 | frase2 | frase3";
        StringBuilder prompt = new StringBuilder();
        prompt.append("Objetivo: ").append(goal.name()).append(". ");
        if (productHint != null && !productHint.isBlank()) {
            prompt.append("Producto/Hint: ").append(productHint).append(". ");
        }
        prompt.append("Revisa la conversación y ofrece coaching en español.\n");
        int idx = 1;
        for (String line : limitedConversation) {
            if (line == null || line.isBlank()) {
                continue;
            }
            prompt.append(idx++).append("). ").append(line.trim()).append('\n');
        }
        GenParams params = new GenParams();
        params.temperature = 0.7;
        params.maxTokens = 380;
        try {
            LLMResult result = llm.generateReply(system, prompt.toString(), limitedConversation, params);
            if (result == null || result.text == null || result.text.isBlank()) {
                return null;
            }
            return parseRefinedAdvice(result.text, base);
        } catch (LLMHttpException httpEx) {
            
            throw httpEx;
        } catch (Exception ex) {
            log.debug("No se pudo refinar con LLM: {}", ex.getMessage());
            return null;
        }
    }

    private Advice parseRefinedAdvice(String text, Advice base) {
        String advice = null;
        String rationale = null;
        String[] phrases = null;
        String[] lines = text.split("\r?\n");
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            String trimmed = line.trim();
            if (trimmed.length() >= 7 && trimmed.regionMatches(true, 0, "ADVICE:", 0, 7)) {
                advice = trimmed.substring(7).trim();
            } else if (trimmed.length() >= 9 && trimmed.regionMatches(true, 0, "RATIONALE:", 0, 9)) {
                rationale = trimmed.substring(9).trim();
            } else if (trimmed.length() >= 8 && trimmed.regionMatches(true, 0, "PHRASES:", 0, 8)) {
                String body = trimmed.substring(8).trim();
                phrases = Arrays.stream(body.split("\\|"))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .toArray(String[]::new);
            }
        }
        String mergedAdvice = safeText(advice, base.advice);
        String mergedRationale = safeText(rationale, base.rationale);
        String[] mergedPhrases = ensurePhrases(phrases != null && phrases.length > 0 ? phrases : base.suggestedPhrases);
        return new Advice(mergedAdvice, mergedRationale, mergedPhrases);
    }

    private List<String> limitConversation(List<String> conversation, int size) {
        if (conversation == null || conversation.isEmpty()) {
            return List.of();
        }
        int from = Math.max(0, conversation.size() - size);
        return new ArrayList<>(conversation.subList(from, conversation.size()));
    }
}
