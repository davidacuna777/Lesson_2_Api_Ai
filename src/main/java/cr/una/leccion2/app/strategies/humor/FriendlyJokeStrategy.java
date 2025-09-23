package cr.una.leccion2.app.strategies.humor;

import java.util.List;

public class FriendlyJokeStrategy implements JokeStrategy {
    @Override
    public String makeJoke(List<String> kws) {
        String seed = kws != null && !kws.isEmpty() ? String.join(", ", kws) : "esto";
        return "😄 Mini-chiste: si " + seed + " fuera un bug, ya tendría su propio club de fans. ¡Vamos con todo!";
    }
}
