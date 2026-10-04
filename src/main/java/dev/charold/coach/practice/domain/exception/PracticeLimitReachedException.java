package dev.charold.coach.practice.domain.exception;

/**
 * Thrown when the instance has used its quota of evaluations for the day.
 */
public class PracticeLimitReachedException extends RuntimeException {

    public PracticeLimitReachedException() {
        super("Daily practice limit reached");
    }
}
