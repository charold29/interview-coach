package dev.charold.coach.practice.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class EvaluationTest {

    @Test
    void levelBandsFollowTheRubric() {
        assertEquals(Level.JUNIOR, Level.fromScore(4.9));
        assertEquals(Level.MID, Level.fromScore(5.0));
        assertEquals(Level.MID, Level.fromScore(6.9));
        assertEquals(Level.SENIOR, Level.fromScore(7.0));
        assertEquals(Level.SENIOR, Level.fromScore(8.4));
        assertEquals(Level.STAFF, Level.fromScore(8.5));
    }

    @Test
    void overallIsTheAverageOfTheFourAxes() {
        Evaluation evaluation = evaluation(8, 6, 7, 6);

        assertEquals(6.75, evaluation.overall());
        assertEquals("6.8", evaluation.overallDisplay());
        assertEquals(Level.MID, evaluation.level());
    }

    @Test
    void scoresOutsideTheScaleAreClamped() {
        Evaluation evaluation = evaluation(14, -3, 10, 10);

        assertEquals(10, evaluation.technicalAccuracy());
        assertEquals(0, evaluation.depth());
    }

    @Test
    void meetsComparesAgainstTheTargetLevel() {
        Evaluation senior = evaluation(8, 7, 7, 7);

        assertTrue(senior.meets(Level.MID));
        assertTrue(senior.meets(Level.SENIOR));
        assertFalse(senior.meets(Level.STAFF));
    }

    @Test
    void nullListsFromTheModelBecomeEmpty() {
        Evaluation evaluation = new Evaluation(false, 5, 5, 5, 5, null, null, null, null, null, null);

        assertEquals(List.of(), evaluation.didWell());
        assertEquals(List.of(), evaluation.englishFixes());
        assertEquals("", evaluation.followUp());
    }

    @Test
    void levelParsingIsLenient() {
        assertEquals(Level.STAFF, Level.parse("staff"));
        assertEquals(Level.MID, Level.parse("Mid"));
        assertEquals(Level.SENIOR, Level.parse("principal"));
        assertEquals(Level.SENIOR, Level.parse(null));
    }

    private static Evaluation evaluation(int accuracy, int depth, int terminology, int clarity) {
        return new Evaluation(false, accuracy, depth, terminology, clarity, "verdict",
                List.of(), List.of(), "answer", List.of(), "follow-up");
    }
}
