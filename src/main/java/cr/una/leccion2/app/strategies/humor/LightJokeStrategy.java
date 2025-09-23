package cr.una.leccion2.app.strategies.humor;

import java.util.List;

public class LightJokeStrategy implements JokeStrategy {
    @Override
    public String makeJoke(List<String> kws) {
        String seed = kws != null && !kws.isEmpty() ? kws.get(0) : "el proyecto";
        return "✨ Humor ligero: " + seed + " pidió vacaciones, pero el deadline no lo dejó.";
    }
}
