package cr.una.leccion2.app.strategies.humor;

import java.util.List;

public class SarcasticJokeStrategy implements JokeStrategy {
    @Override
    public String makeJoke(List<String> kws) {
        String seed = kws != null && !kws.isEmpty() ? kws.get(0) : "eso";
        return "🙃 Sarcasmo amable: claro, " + seed + " se arregla solo… como siempre. (Igual vamos bien 😅)";
    }
}
