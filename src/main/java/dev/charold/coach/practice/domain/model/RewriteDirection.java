package dev.charold.coach.practice.domain.model;

/**
 * How a model answer should change when the candidate asks for another version.
 */
public enum RewriteDirection {
    SHORTER,
    LONGER;

    /** Lenient parse for form input; anything other than "longer" means shorter. */
    public static RewriteDirection parse(String value) {
        return value != null && "longer".equalsIgnoreCase(value.strip()) ? LONGER : SHORTER;
    }
}
