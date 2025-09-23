package cr.una.leccion2.app.strategies.sales;

import java.util.List;

public class MotivatePurchaseStrategy implements SalesStrategy {
    @Override
    public String advice(List<String> conv, String hint) {
        return "Refuerza beneficios clave, reduce fricciones (envío/devoluciones sencillas), añade llamada a la acción clara.";
    }

    @Override
    public String rationale(List<String> conv, String hint) {
        return "La simplificación del proceso y la claridad del valor aumentan la conversión sin presión agresiva.";
    }

    @Override
    public String[] suggestedPhrases(List<String> conv, String hint) {
        return new String[]{
            "Te llega rápido y tenés 7 días para cambios, sin complicaciones.",
            "La mayoría de clientes valora X y Y; en tu caso suma bastante.",
            "¿Te envío el link de compra ahora o preferís que agende una demo breve?"
        };
    }
}
