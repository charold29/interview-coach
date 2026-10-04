package dev.charold.coach.practice.infrastructure.adapter.out.ai;

import java.util.Objects;

import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;

/**
 * LangChain4jEvaluatorAdapter
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public class LangChain4jEvaluatorAdapter implements AnswerEvaluatorPort {

    private final InterviewCoach coach;

    public LangChain4jEvaluatorAdapter(InterviewCoach coach) {
        this.coach = Objects.requireNonNull(coach);
    }

    @Override
    public Evaluation evaluate(PracticeProfile profile, String question, String answer) {
        return AiEvaluationMapper.toDomain(coach.evaluate(profile, question, answer));
    }

    @Override
    public String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction) {
        String rewritten = coach.rewrite(profile, question, currentAnswer, prompt(direction));
        return rewritten == null ? "" : rewritten.strip();
    }

    static String prompt(RewriteDirection direction) {
        return switch (direction) {
            case SHORTER -> "shorter";
            case LONGER -> "longer";
        };
    }
}
