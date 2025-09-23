package cr.una.leccion2.app;

import cr.una.leccion2.app.strategies.sales.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

public class SalesStrategiesTest {

    @Test
    void rejectReturnGivesContent() {
        SalesStrategy s = new RejectReturnStrategy();
        Assertions.assertThat(s.advice(List.of("a"), null)).isNotBlank();
        Assertions.assertThat(s.rationale(List.of("a"), null)).isNotBlank();
        Assertions.assertThat(s.suggestedPhrases(List.of("a"), null)).isNotEmpty();
    }

    @Test
    void upsellGivesContent() {
        SalesStrategy s = new UpsellStrategy();
        Assertions.assertThat(s.advice(List.of("a"), "Pro")).contains("Pro");
        Assertions.assertThat(s.suggestedPhrases(List.of("a"), "Pro")[0]).contains("Pro");
    }

    @Test
    void motivateGivesContent() {
        SalesStrategy s = new MotivatePurchaseStrategy();
        Assertions.assertThat(s.advice(List.of("a"), null)).isNotBlank();
    }
}
