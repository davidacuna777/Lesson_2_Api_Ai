package cr.una.leccion2.app.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoachControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void setupEnv() {
        System.setProperty("PROVIDER", "mock");
        System.setProperty("SALES_COACH_USE_LLM", "false");
    }

    @Test
    void analyzeReturnsAdviceForMockProvider() throws Exception {
        String body = "{" +
                "\"goal\":\"MOTIVATE\"," +
                "\"conversation\":[{" +
                "\"role\":\"user\",\"content\":\"Necesito motivación para cerrar\"},{" +
                "\"role\":\"seller\",\"content\":\"Te muestro beneficios\"}]," +
                "\"productHint\":\"Kit Deluxe\"}";

        mockMvc.perform(post("/coach/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.advice").isNotEmpty())
                .andExpect(jsonPath("$.rationale").isNotEmpty())
                .andExpect(jsonPath("$.suggestedPhrases").isArray());
    }

    @Test
    void analyzeFailsWithInvalidGoal() throws Exception {
        String body = "{" +
                "\"goal\":\"UNKNOWN\"," +
                "\"conversation\":[{" +
                "\"role\":\"user\",\"content\":\"Hola\"}]}";

        mockMvc.perform(post("/coach/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_GOAL"));
    }
}