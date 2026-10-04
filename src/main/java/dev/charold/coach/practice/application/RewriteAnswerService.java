package dev.charold.coach.practice.application;

import java.util.Objects;

import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;
import dev.charold.coach.practice.domain.port.in.RewriteAnswerUseCase;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;
import dev.charold.coach.practice.domain.port.out.UsageQuotaPort;

/**
 * Plain Java: no framework annotations. Wired in infrastructure/config.
 */
public class RewriteAnswerService implements RewriteAnswerUseCase {

    private final AnswerEvaluatorPort evaluator;
    private final UsageQuotaPort quota;

    public RewriteAnswerService(AnswerEvaluatorPort evaluator, UsageQuotaPort quota) {
        this.evaluator = Objects.requireNonNull(evaluator);
        this.quota = Objects.requireNonNull(quota);
    }

    @Override
    public String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction) {
        if (!quota.tryAcquire()) {
            throw new PracticeLimitReachedException();
        }
        return evaluator.rewrite(profile, question, currentAnswer, direction);
    }
}
