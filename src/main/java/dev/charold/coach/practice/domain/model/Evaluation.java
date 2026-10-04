package dev.charold.coach.practice.domain.model;

import java.util.List;
import java.util.Locale;

/**
 * Evaluation
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public record Evaluation(
        boolean technicallyIncorrect,
        int technicalAccuracy,
        int depth,
        int terminology,
        int clarity,
        String verdict,
        List<String> didWell,
        List<String> missing,
        String modelAnswer,
        List<EnglishFix> englishFixes,
        String followUp) {

    public Evaluation {
        technicalAccuracy = clamp(technicalAccuracy);
        depth = clamp(depth);
        terminology = clamp(terminology);
        clarity = clamp(clarity);
        verdict = verdict == null ? "" : verdict;
        didWell = didWell == null ? List.of() : List.copyOf(didWell);
        missing = missing == null ? List.of() : List.copyOf(missing);
        modelAnswer = modelAnswer == null ? "" : modelAnswer;
        englishFixes = englishFixes == null ? List.of() : List.copyOf(englishFixes);
        followUp = followUp == null ? "" : followUp;
    }

    /** Average of the four axes. */
    public double overall() {
        return (technicalAccuracy + depth + terminology + clarity) / 4.0;
    }

    /** Overall score formatted with one decimal, e.g. "7.3". */
    public String overallDisplay() {
        return String.format(Locale.ROOT, "%.1f", overall());
    }

    public Level level() {
        return Level.fromScore(overall());
    }

    /** True when the answer reaches (or exceeds) the level the candidate is targeting. */
    public boolean meets(Level target) {
        return level().ordinal() >= target.ordinal();
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(value, 10));
    }
}
