package cr.una.leccion2.app.strategies.humor;

import java.util.List;

public interface JokeStrategy {
    String makeJoke(List<String> recentKeywords);
}
