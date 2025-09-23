package cr.una.leccion2.app;

import cr.una.leccion2.app.services.MessageCounter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageCounterTest {
    @Test
    void incrementsPerChat() {
        MessageCounter c = new MessageCounter();
        Assertions.assertThat(c.increment("a")).isEqualTo(1);
        Assertions.assertThat(c.increment("a")).isEqualTo(2);
        Assertions.assertThat(c.increment("b")).isEqualTo(1);
    }
}
