package dev.charold.coach.web;

import org.jboss.resteasy.reactive.RestForm;

import dev.charold.coach.domain.Level;
import dev.charold.coach.domain.PracticeProfile;

/**
 * Fields posted by the practice form. The profile fields travel with every
 * request because the server keeps no session: they live in the browser.
 */
public class PracticeForm {

    static final int MAX_QUESTION = 1_000;
    static final int MAX_ANSWER = 4_000;

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

    PracticeProfile profile() {
        return new PracticeProfile(
                truncate(role, 100),
                Level.parse(targetLevel),
                parseYears(years),
                truncate(stack, 300),
                truncate(feedbackLanguage, 40));
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
