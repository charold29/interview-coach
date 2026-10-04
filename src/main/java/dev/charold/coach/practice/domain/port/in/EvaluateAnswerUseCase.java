package dev.charold.coach.practice.domain.port.in;

import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;

/**
 * EvaluateAnswerUseCase
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public interface EvaluateAnswerUseCase {

    /**
     * @throws PracticeLimitReachedException when today's quota is used up
     */
    Evaluation evaluate(PracticeProfile profile, String question, String answer);
}
