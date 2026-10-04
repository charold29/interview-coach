package dev.charold.coach.practice.infrastructure.adapter.out.ai.dto;

import java.util.List;

/**
 * The JSON shape the language model fills in. Field names are part of the
 * prompt contract (the system prompt refers to them), so change them together
 * with {@code InterviewCoach}.
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

    public record AiEnglishFixDto(String youSaid, String better, String why) {
    }
}
