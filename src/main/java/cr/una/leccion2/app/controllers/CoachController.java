package cr.una.leccion2.app.controllers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.una.leccion2.app.domain.dto.CoachAnalyzeRequest;
import cr.una.leccion2.app.domain.dto.CoachAnalyzeResponse;
import cr.una.leccion2.app.domain.dto.MessageDto;
import cr.una.leccion2.app.factory.UseCaseFactory;
import cr.una.leccion2.app.usecases.SalesCoach;

@RestController
@RequestMapping("/coach")
public class CoachController {

    private final UseCaseFactory factory;

    public CoachController(UseCaseFactory factory) {
        this.factory = factory;
    }

    @PostMapping("/analyze")
    public ResponseEntity<CoachAnalyzeResponse> analyze(@RequestBody CoachAnalyzeRequest request) throws Exception {
        if (request == null || request.goal == null) {
            throw new IllegalArgumentException("INVALID_GOAL");
        }
        SalesCoach coach = factory.salesCoach();
        SalesCoach.Goal goal = SalesCoach.parseGoal(request.goal);
        List<String> conversation = toConversationLines(request.conversation);

        SalesCoach.Advice advice = coach.advise(conversation, goal, request.productHint);
        CoachAnalyzeResponse response = new CoachAnalyzeResponse();
        response.advice = advice.advice;
        response.rationale = advice.rationale;
        response.suggestedPhrases = advice.suggestedPhrases;
        return ResponseEntity.ok(response);
    }

    private List<String> toConversationLines(List<MessageDto> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }
        return messages.stream()
                .filter(Objects::nonNull)
                .map(msg -> {
                    String role = msg.role != null ? msg.role.trim() : "";
                    String content = msg.content != null ? msg.content.trim() : "";
                    return role.isEmpty() ? content : role + ": " + content;
                })
                .collect(Collectors.toList());
    }
}