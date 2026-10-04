package dev.charold.coach.practice.domain.port.in;

import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;

/**
 * RewriteAnswerUseCase
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public interface RewriteAnswerUseCase {

    /**
     * @throws PracticeLimitReachedException when today's quota is used up
     */
    String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction);
}
