package dev.charold.coach.practice.infrastructure.adapter.out.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.Level;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.dto.AiEvaluationDto;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.dto.AiEvaluationDto.AiEnglishFixDto;

class AiEvaluationMapperTest {

    @Test
    void mapsEveryField() {
        AiEvaluationDto dto = new AiEvaluationDto(true, 3, 4, 5, 6, "Wrong about TTLs.",
                List.of("good"), List.of("missing"), "model",
                List.of(new AiEnglishFixDto("depends of", "depends on", "preposition")), "next?");

        Evaluation evaluation = AiEvaluationMapper.toDomain(dto);

        assertTrue(evaluation.technicallyIncorrect());
        assertEquals(4.5, evaluation.overall());
        assertEquals(Level.JUNIOR, evaluation.level());
        assertEquals("Wrong about TTLs.", evaluation.verdict());
        assertEquals(List.of("good"), evaluation.didWell());
        assertEquals("depends on", evaluation.englishFixes().get(0).better());
        assertEquals("next?", evaluation.followUp());
    }

    @Test
    void toleratesSloppyModelOutput() {
        AiEvaluationDto dto = new AiEvaluationDto(false, 12, -1, 7, 7, null, null, null, null,
                Arrays.asList(null, new AiEnglishFixDto(" ", "x", "y"), new AiEnglishFixDto("a", "b", "c")), null);

        Evaluation evaluation = AiEvaluationMapper.toDomain(dto);

        assertEquals(10, evaluation.technicalAccuracy());
        assertEquals(0, evaluation.depth());
        assertEquals(List.of(), evaluation.missing());
        assertEquals(1, evaluation.englishFixes().size());
        assertEquals("", evaluation.modelAnswer());
    }

    @Test
    void failsLoudlyWhenTheModelReturnsNothing() {
        assertThrows(IllegalStateException.class, () -> AiEvaluationMapper.toDomain(null));
    }
}
