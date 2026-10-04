package dev.charold.coach.practice.domain.exception;

/**
 * PracticeLimitReachedException
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public class PracticeLimitReachedException extends RuntimeException {

    public PracticeLimitReachedException() {
        super("Daily practice limit reached");
    }
}
