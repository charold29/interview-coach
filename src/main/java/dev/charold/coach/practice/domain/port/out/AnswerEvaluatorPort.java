package dev.charold.coach.practice.domain.port.out;

import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;

/**
 * AnswerEvaluatorPort
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public interface AnswerEvaluatorPort {

    Evaluation evaluate(PracticeProfile profile, String question, String answer);

    String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction);
}
