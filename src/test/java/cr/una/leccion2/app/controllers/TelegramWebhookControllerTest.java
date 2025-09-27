package cr.una.leccion2.app.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

import cr.una.leccion2.app.adapters.TelegramAdapter;
import cr.una.leccion2.app.config.ConfigService;
import cr.una.leccion2.app.factory.UseCaseFactory;
import cr.una.leccion2.app.llm.LLMResult;
import cr.una.leccion2.app.services.ConversationContextService;
import cr.una.leccion2.app.usecases.ChatAgent;
import cr.una.leccion2.app.usecases.HumorAgent;

class TelegramWebhookControllerTest {

    static class CapturingTelegramAdapter extends TelegramAdapter {
        final List<String> chatIds = new ArrayList<>();
        final List<String> messages = new ArrayList<>();

        CapturingTelegramAdapter() {
            super(new ConfigService());
        }

        @Override
        public void sendMessage(String chatId, String text) {
            chatIds.add(chatId);
            messages.add(text);
        }
    }

    @Test
    void repliesUsingChatAgentAndStoresConversation() throws Exception {
        CapturingTelegramAdapter telegramAdapter = new CapturingTelegramAdapter();
        ConversationContextService contextService = new ConversationContextService();
        UseCaseFactory factory = mock(UseCaseFactory.class);
        ChatAgent chatAgent = mock(ChatAgent.class);
        HumorAgent humorAgent = mock(HumorAgent.class);

        when(factory.chatAgent()).thenReturn(chatAgent);
        when(factory.humorAgent()).thenReturn(humorAgent);
        when(chatAgent.reply(anyString(), eq("hola"), anyList(), isNull())).thenReturn(new LLMResult("respuesta", 0, 0));
        when(humorAgent.maybeTellJoke("42")).thenReturn(null);

        TelegramWebhookController controller = new TelegramWebhookController(
                telegramAdapter,
                factory,
                contextService
        );

        String payload = "{\"message\":{\"chat\":{\"id\":\"42\"},\"text\":\"hola\"}}";

        ResponseEntity<Map<String, Object>> response = controller.webhook(payload);

        assertThat(response.getBody()).containsEntry("status", "ok");
        assertThat(telegramAdapter.chatIds).containsExactly("42");
        assertThat(telegramAdapter.messages).containsExactly("respuesta");

        List<String> stored = contextService.lastMessages("42", 10);
        assertThat(stored).containsExactly("hola", "respuesta");

        verify(humorAgent).maybeTellJoke("42");
    }
}