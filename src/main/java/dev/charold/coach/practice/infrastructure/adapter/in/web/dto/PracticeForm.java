package dev.charold.coach.practice.infrastructure.adapter.in.web.dto;

import org.jboss.resteasy.reactive.RestForm;

import dev.charold.coach.practice.domain.model.Level;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;

/**
 * PracticeForm
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public class PracticeForm {

    public static final int MAX_QUESTION = 1_000;
    public static final int MAX_ANSWER = 4_000;

    @RestForm
    public String role;

    @RestForm
    public String targetLevel;

    @RestForm
    public String years;

    @RestForm
    public String stack;

    @RestForm
    public String feedbackLanguage;

    @RestForm
    public String accessCode;

    @RestForm
    public String question;

    @RestForm
    public String answer;

    /** The model answer currently shown, sent back by the shorter/longer buttons. */
    @RestForm
    public String currentAnswer;

    @RestForm
    public String direction;

    public PracticeProfile toProfile() {
        return new PracticeProfile(
                truncate(role, 100),
                Level.parse(targetLevel),
                parseYears(years),
                truncate(stack, 300),
                truncate(feedbackLanguage, 40));
    }

    public RewriteDirection toDirection() {
        return RewriteDirection.parse(direction);
    }

    static String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        String stripped = value.strip();
        return stripped.length() <= max ? stripped : stripped.substring(0, max);
    }

    private static int parseYears(String value) {
        try {
            return value == null || value.isBlank() ? 0 : Integer.parseInt(value.strip());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
