package cr.una.leccion2.app.services;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConversationContextServiceTest {

    private final ConversationContextService service = new ConversationContextService();

    @Test
    void appendMessageKeepsBoundedHistory() {
        String chatId = "team-standup";
        for (int i = 1; i <= 40; i++) {
            service.appendMessage(chatId, "mensaje " + i);
        }
        List<String> history = service.lastMessages(chatId, 50);
        assertThat(history).hasSize(30);
        assertThat(history.getFirst()).isEqualTo("mensaje 11");
        assertThat(history.getLast()).isEqualTo("mensaje 40");
    }

    @Test
    void extractKeywordsReturnsMostRelevantTokens() {
        List<String> messages = List.of(
                "Cliente consulta por laptop gamer",
                "Explica beneficios de laptop con GPU dedicada",
                "Laptop incluye garantía extendida"
        );
        List<String> keywords = service.extractKeywords(messages, 3);
        assertThat(keywords).contains("laptop");
        assertThat(keywords.getFirst()).isEqualTo("laptop");
    }

    @Test
    void extractKeywordsFromConversationUsesStoredHistory() {
        String chatId = "ventas";
        service.appendMessage(chatId, "Cliente pide devolución por teclado");
        service.appendMessage(chatId, "Ofrece cambio a teclado mecánico premium");
        List<String> keywords = service.extractKeywords(chatId, 2);
        assertThat(keywords).isNotEmpty();
        assertThat(keywords.getFirst()).containsAnyOf("teclado", "cliente", "cambio");
    }
}
