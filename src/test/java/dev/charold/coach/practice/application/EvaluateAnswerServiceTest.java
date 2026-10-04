package dev.charold.coach.practice.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dev.charold.coach.practice.application.Fakes.FixedQuota;
import dev.charold.coach.practice.application.Fakes.RecordingEvaluator;
import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.Evaluation;

class EvaluateAnswerServiceTest {

    private final RecordingEvaluator evaluator = new RecordingEvaluator();

    @Test
    void delegatesToTheEvaluatorWhenThereIsQuota() {
        FixedQuota quota = new FixedQuota(1);
        EvaluateAnswerService service = new EvaluateAnswerService(evaluator, quota);

        Evaluation result = service.evaluate(Fakes.profile(), "Why cache?", "Because it's faster.");

        assertSame(RecordingEvaluator.RESULT, result);
        assertEquals("Why cache?", evaluator.lastQuestion);
        assertEquals(0, quota.remaining());
    }

    @Test
    void refusesWithoutCallingTheEvaluatorWhenTheQuotaIsUsedUp() {
        EvaluateAnswerService service = new EvaluateAnswerService(evaluator, new FixedQuota(0));

        assertThrows(PracticeLimitReachedException.class,
                () -> service.evaluate(Fakes.profile(), "Why cache?", "Because it's faster."));
        assertEquals(0, evaluator.evaluateCalls);
    }
}
