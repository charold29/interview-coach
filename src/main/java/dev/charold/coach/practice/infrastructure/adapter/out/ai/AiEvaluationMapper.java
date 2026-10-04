package dev.charold.coach.practice.infrastructure.adapter.out.ai;

import java.util.List;

import dev.charold.coach.practice.domain.model.EnglishFix;
import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.dto.AiEvaluationDto;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.dto.AiEvaluationDto.AiEnglishFixDto;

/**
 * Translates the model's JSON contract into the domain. Model output is
 * untrusted: missing lists and fixes become empty, blank fixes are dropped,
 * and the domain record clamps scores to 0-10.
 */
final class AiEvaluationMapper {

    private AiEvaluationMapper() {
    }

    static Evaluation toDomain(AiEvaluationDto dto) {
        if (dto == null) {
            throw new IllegalStateException("The model returned no evaluation");
        }
        return new Evaluation(
                dto.technicallyIncorrect(),
                dto.technicalAccuracy(),
                dto.depth(),
                dto.terminology(),
                dto.clarity(),
                dto.verdict(),
                dto.didWell(),
                dto.missing(),
                dto.modelAnswer(),
                toDomain(dto.englishFixes()),
                dto.followUp());
    }

    private static List<EnglishFix> toDomain(List<AiEnglishFixDto> fixes) {
        if (fixes == null) {
            return List.of();
        }
        return fixes.stream()
                .filter(fix -> fix != null && fix.youSaid() != null && !fix.youSaid().isBlank())
                .map(fix -> new EnglishFix(fix.youSaid(), fix.better(), fix.why()))
                .toList();
    }
}
