package cr.una.leccion2.app.strategies.sales;

import java.util.List;

public class RejectReturnStrategy implements SalesStrategy {
    @Override
    public String advice(List<String> conv, String hint) {
        return "Explica con empatía por qué no procede la devolución según política y ofrece alternativas: diagnóstico técnico, crédito o capacitación breve.";
    }

    @Override
    public String rationale(List<String> conv, String hint) {
        return "Minimizas fricción, cuidas la relación y mantienes márgenes sin sentar precedentes de reembolsos improcedentes.";
    }

    @Override
    public String[] suggestedPhrases(List<String> conv, String hint) {
        return new String[]{
            "Entiendo tu situación; según nuestra garantía, puedo ofrecerte una revisión sin costo.",
            "Si te parece, te doy un crédito equivalente para tu próxima compra.",
            "Puedo mostrarte cómo sacarle más provecho para resolver lo que mencionaste."
        };
    }
}
