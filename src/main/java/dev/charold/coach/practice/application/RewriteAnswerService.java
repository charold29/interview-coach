package dev.charold.coach.practice.application;

import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;
import dev.charold.coach.practice.domain.port.in.RewriteAnswerUseCase;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;
import dev.charold.coach.practice.domain.port.out.UsageQuotaPort;

/**
 * RewriteAnswerService
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public class RewriteAnswerService implements RewriteAnswerUseCase {

    private final AnswerEvaluatorPort evaluator;
    private final UsageQuotaPort quota;

    public RewriteAnswerService(AnswerEvaluatorPort evaluator, UsageQuotaPort quota) {
        this.evaluator = evaluator;
        this.quota = quota;
    }

    @Override
    public String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction) {
        if (!quota.tryAcquire()) {
            throw new PracticeLimitReachedException();
        }
        return evaluator.rewrite(profile, question, currentAnswer, direction);
    }
}
