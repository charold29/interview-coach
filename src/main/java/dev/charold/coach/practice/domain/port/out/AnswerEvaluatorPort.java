package dev.charold.coach.practice.domain.port.out;

import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;

/**
 * Driven port: whatever does the actual judging. Today a language model or a
 * canned mock; the domain doesn't know which.
 */
public interface AnswerEvaluatorPort {

    Evaluation evaluate(PracticeProfile profile, String question, String answer);

    String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction);
}
