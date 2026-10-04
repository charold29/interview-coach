package dev.charold.coach.practice.infrastructure.adapter.out.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.dto.AiEvaluationDto;

/**
 * The AI service is just an interface, so a plain stub stands in for the model.
 */
class LangChain4jEvaluatorAdapterTest {

    private static final PracticeProfile PROFILE = new PracticeProfile(null, null, 0, null, null);

    @Test
    void mapsTheModelResponseToTheDomain() {
        LangChain4jEvaluatorAdapter adapter = new LangChain4jEvaluatorAdapter(new StubCoach("unused"));

        assertEquals("7.0", adapter.evaluate(PROFILE, "q", "a").overallDisplay());
    }

    @Test
    void sendsTheDirectionAsPromptTextAndTrimsTheResult() {
        StubCoach coach = new StubCoach("  Shorter answer.\n");
        LangChain4jEvaluatorAdapter adapter = new LangChain4jEvaluatorAdapter(coach);

        String result = adapter.rewrite(PROFILE, "q", "long answer", RewriteDirection.SHORTER);

        assertEquals("Shorter answer.", result);
        assertEquals("shorter", coach.lastDirection);
    }

    @Test
    void anEmptyModelReplyBecomesAnEmptyAnswer() {
        LangChain4jEvaluatorAdapter adapter = new LangChain4jEvaluatorAdapter(new StubCoach(null));

        assertEquals("", adapter.rewrite(PROFILE, "q", "a", RewriteDirection.LONGER));
    }

    private static final class StubCoach implements InterviewCoach {
        private final String rewriteReply;
        String lastDirection;

        StubCoach(String rewriteReply) {
            this.rewriteReply = rewriteReply;
        }

        @Override
        public AiEvaluationDto evaluate(PracticeProfile profile, String question, String answer) {
            return new AiEvaluationDto(false, 7, 7, 7, 7, "ok", List.of(), List.of(), "m", List.of(), "f");
        }

        @Override
        public String rewrite(PracticeProfile profile, String question, String currentAnswer, String direction) {
            lastDirection = direction;
            return rewriteReply;
        }
    }
}
