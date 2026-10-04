package dev.charold.coach.practice.domain.port.in;

import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;

/**
 * Driver port: produce a shorter or longer version of a model answer.
 */
public interface RewriteAnswerUseCase {

    /**
     * @throws PracticeLimitReachedException when today's quota is used up
     */
    String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction);
}
