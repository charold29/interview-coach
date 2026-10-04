package dev.charold.coach.practice.domain.model;

/**
 * RewriteDirection
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public enum RewriteDirection {
    SHORTER,
    LONGER;

    /** Lenient parse for form input; anything other than "longer" means shorter. */
    public static RewriteDirection parse(String value) {
        return value != null && "longer".equalsIgnoreCase(value.strip()) ? LONGER : SHORTER;
    }
}
