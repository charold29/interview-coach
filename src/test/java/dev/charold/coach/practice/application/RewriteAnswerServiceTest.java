package dev.charold.coach.practice.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dev.charold.coach.practice.application.Fakes.FixedQuota;
import dev.charold.coach.practice.application.Fakes.RecordingEvaluator;
import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.RewriteDirection;

class RewriteAnswerServiceTest {

    private final RecordingEvaluator evaluator = new RecordingEvaluator();

    @Test
    void delegatesWithTheRequestedDirection() {
        RewriteAnswerService service = new RewriteAnswerService(evaluator, new FixedQuota(1));

        String result = service.rewrite(Fakes.profile(), "Why cache?", "Long answer.", RewriteDirection.LONGER);

        assertEquals("rewritten LONGER", result);
        assertEquals(RewriteDirection.LONGER, evaluator.lastDirection);
    }

    @Test
    void rewritesCountAgainstTheSameQuota() {
        FixedQuota quota = new FixedQuota(1);
        RewriteAnswerService service = new RewriteAnswerService(evaluator, quota);

        service.rewrite(Fakes.profile(), "Why cache?", "Answer.", RewriteDirection.SHORTER);

        assertThrows(PracticeLimitReachedException.class,
                () -> service.rewrite(Fakes.profile(), "Why cache?", "Answer.", RewriteDirection.SHORTER));
        assertEquals(1, evaluator.rewriteCalls);
    }
}
