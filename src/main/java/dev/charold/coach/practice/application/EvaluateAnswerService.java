package dev.charold.coach.practice.application;

import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.port.in.EvaluateAnswerUseCase;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;
import dev.charold.coach.practice.domain.port.out.UsageQuotaPort;
import lombok.RequiredArgsConstructor;

/**
 * EvaluateAnswerService
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
@RequiredArgsConstructor
public class EvaluateAnswerService implements EvaluateAnswerUseCase {

    private final AnswerEvaluatorPort evaluator;
    private final UsageQuotaPort quota;

    @Override
    public Evaluation evaluate(PracticeProfile profile, String question, String answer) {
        if (!quota.tryAcquire()) {
            throw new PracticeLimitReachedException();
        }
        return evaluator.evaluate(profile, question, answer);
    }
}
