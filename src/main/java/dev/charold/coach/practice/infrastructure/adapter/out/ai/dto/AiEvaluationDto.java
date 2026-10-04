package dev.charold.coach.practice.infrastructure.adapter.out.ai.dto;

import java.util.List;

/**
 * AiEvaluationDto
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public record AiEvaluationDto(
        boolean technicallyIncorrect,
        int technicalAccuracy,
        int depth,
        int terminology,
        int clarity,
        String verdict,
        List<String> didWell,
        List<String> missing,
        String modelAnswer,
        List<AiEnglishFixDto> englishFixes,
        String followUp) {

    /**
     * AiEnglishFixDto
     *
     * @author Harold Rojas Plasencia
     * @since 2026-10-03
     */
    public record AiEnglishFixDto(String youSaid, String better, String why) {
    }
}
