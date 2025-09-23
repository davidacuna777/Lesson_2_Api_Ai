package cr.una.leccion2.app.usecases;

import cr.una.leccion2.app.llm.LLMClient;
import cr.una.leccion2.app.llm.LLMResult;
import cr.una.leccion2.app.strategies.sales.SalesStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SalesCoachTest {

    private static class StubStrategy implements SalesStrategy {
        private final String advice;
        private final String rationale;
        private final String[] phrases;

        StubStrategy(String advice, String rationale, String... phrases) {
            this.advice = advice;
            this.rationale = rationale;
            this.phrases = phrases;
        }

        @Override
        public String advice(List<String> conversation, String productHint) {
            return advice;
        }

        @Override
        public String rationale(List<String> conversation, String productHint) {
            return rationale;
        }

        @Override
        public String[] suggestedPhrases(List<String> conversation, String productHint) {
            return phrases;
        }
    }

    @Test
    void adviseUsesSelectedStrategyWhenRefineDisabled() throws Exception {
        SalesStrategy reject = new StubStrategy("rechaza con empatía", "protege margen", "opción A");
        SalesStrategy upsell = new StubStrategy("promueve premium", "mejora ticket", "opción B");
        SalesStrategy motivate = new StubStrategy("motiva compra", "cierra venta", "opción C");
        LLMClient mockLlm = (system, user, context, params) -> new LLMResult("", 0, 0);
        SalesCoach coach = new SalesCoach(mockLlm, reject, upsell, motivate, false);

        SalesCoach.Advice result = coach.advise(List.of("Cliente pregunta por plan básico"), SalesCoach.Goal.UPSELL, "Plan Premium");

        assertThat(result.advice).isEqualTo("promueve premium");
        assertThat(result.rationale).isEqualTo("mejora ticket");
        assertThat(result.suggestedPhrases).containsExactly("opción B");
    }

    @Test
    void adviseMergesRefinementWhenEnabled() throws Exception {
        SalesStrategy strategy = new StubStrategy("mensaje base", "racional base", "frase 1");
        LLMClient llm = (system, prompt, context, params) -> new LLMResult(
                "ADVICE: Insiste en el plan Premium\n" +
                        "RATIONALE: Alinea beneficios con el dolor del cliente\n" +
                        "PHRASES: ¿Te gustaría probarlo? | Te envío el upgrade | Garantía de cambio",
                100,
                120
        );
        SalesCoach coach = new SalesCoach(llm, strategy, strategy, strategy, true);

        SalesCoach.Advice result = coach.advise(List.of("Cliente duda del plan básico"), SalesCoach.Goal.UPSELL, "Plan Premium");

        assertThat(result.advice).contains("plan Premium");
        assertThat(result.rationale).contains("beneficios");
        assertThat(result.suggestedPhrases).containsExactly(
                "¿Te gustaría probarlo?",
                "Te envío el upgrade",
                "Garantía de cambio"
        );
    }

    @Test
    void adviseFallsBackToBaseWhenLlmReturnsNoise() throws Exception {
        SalesStrategy strategy = new StubStrategy("base", "racional", "frase base");
        LLMClient noisy = (system, prompt, context, params) -> new LLMResult("sin formato", 0, 0);
        SalesCoach coach = new SalesCoach(noisy, strategy, strategy, strategy, true);

        SalesCoach.Advice result = coach.advise(List.of("Cliente pide reembolso"), SalesCoach.Goal.REJECT, null);

        assertThat(result.advice).isEqualTo("base");
        assertThat(result.suggestedPhrases).containsExactly("frase base");
    }
}