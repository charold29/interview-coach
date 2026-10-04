package dev.charold.coach.practice.infrastructure.adapter.out.ai;

import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.dto.AiEvaluationDto;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

/**
 * InterviewCoach
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
@RegisterAiService(chatMemoryProviderSupplier = RegisterAiService.NoChatMemoryProviderSupplier.class)
public interface InterviewCoach {

    @SystemMessage(AiPrompts.RUBRIC)
    @UserMessage("""
            Candidate profile:
            - Target role: {profile.role}
            - Target level: {profile.targetLevel.label}
            - Years of experience: {profile.yearsOfExperience}
            - Stack: {profile.stack}
            - Feedback language for englishFixes.why: {profile.feedbackLanguage}

            Interview question:
            <question>
            {question}
            </question>

            Candidate's answer:
            <answer>
            {answer}
            </answer>
            """)
    AiEvaluationDto evaluate(PracticeProfile profile, String question, String answer);

    @SystemMessage(AiPrompts.REWRITE_SYSTEM)
    @UserMessage("""
            Target role: {profile.role}, level {profile.targetLevel.label}.

            Interview question:
            <question>
            {question}
            </question>

            Current model answer:
            <answer>
            {currentAnswer}
            </answer>

            Rewrite it to be {direction}.
            Shorter means 2-3 sentences: the direct answer and the single most important
            trade-off. Longer means 6-9 sentences: add a concrete number or failure mode
            and connect it to a real architecture decision. Never pad with filler.
            """)
    String rewrite(PracticeProfile profile, String question, String currentAnswer, String direction);
}
