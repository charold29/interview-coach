package dev.charold.coach.practice.domain.model;

/**
 * PracticeProfile
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public record PracticeProfile(
        String role,
        Level targetLevel,
        int yearsOfExperience,
        String stack,
        String feedbackLanguage) {

    public PracticeProfile {
        role = blankTo(role, "Backend Engineer");
        targetLevel = targetLevel == null ? Level.SENIOR : targetLevel;
        yearsOfExperience = Math.max(0, Math.min(yearsOfExperience, 50));
        stack = blankTo(stack, "not specified");
        feedbackLanguage = blankTo(feedbackLanguage, "English");
    }

    private static String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.strip();
    }
}
