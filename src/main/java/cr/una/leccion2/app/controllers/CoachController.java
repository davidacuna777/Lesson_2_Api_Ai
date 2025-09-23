package cr.una.leccion2.app.controllers;

import cr.una.leccion2.app.domain.dto.CoachAnalyzeRequest;
import cr.una.leccion2.app.domain.dto.CoachAnalyzeResponse;
import cr.una.leccion2.app.factory.UseCaseFactory;
import cr.una.leccion2.app.usecases.SalesCoach;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/coach")
public class CoachController {

    private final UseCaseFactory factory;
    public CoachController(UseCaseFactory factory) { this.factory = factory; }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody CoachAnalyzeRequest req) {
        try {
            SalesCoach coach = factory.salesCoach();
            List<String> conv = new ArrayList<>();
            if (req.conversation != null) {
                for (var m : req.conversation) {
                    if (m != null && m.content != null) conv.add(m.content);
                }
            }
            SalesCoach.Goal goal = SalesCoach.Goal.valueOf(req.goal == null ? "MOTIVATE" : req.goal);
            var advice = coach.advise(conv, goal, req.productHint);
            CoachAnalyzeResponse out = new CoachAnalyzeResponse();
            out.advice = advice.advice;
            out.rationale = advice.rationale;
            out.suggestedPhrases = advice.suggestedPhrases;
            return ResponseEntity.ok(out);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(
                java.util.Map.of("errorCode","COACH_ERROR","message",ex.getMessage(),"hint","Verificá el goal: REJECT|UPSELL|MOTIVATE")
            );
        }
    }
}
