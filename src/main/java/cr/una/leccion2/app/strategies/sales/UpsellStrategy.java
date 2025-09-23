package cr.una.leccion2.app.strategies.sales;

import java.util.List;

public class UpsellStrategy implements SalesStrategy {
    @Override
    public String advice(List<String> conv, String hint) {
        String target = hint != null && !hint.isBlank() ? hint : "la versión Pro";
        return "Conecta necesidades detectadas con beneficios concretos y ofrece " + target + " destacando garantía y soporte.";
    }

    @Override
    public String rationale(List<String> conv, String hint) {
        return "Propuesta de valor clara reduce objeciones; mejoras tangibles justifican el diferencial de precio.";
    }

    @Override
    public String[] suggestedPhrases(List<String> conv, String hint) {
        String target = hint != null && !hint.isBlank() ? hint : "el modelo superior";
        return new String[]{
            "Por lo que comentas, " + target + " te da mejor desempeño y durabilidad.",
            "Incluye soporte extendido y garantía adicional, ideal para tu caso.",
            "Si querés, te preparo una oferta con envío rápido."
        };
    }
}
