package dev.charold.coach.practice.domain.port.out;

/**
 * Driven port: a budget of evaluator calls. In memory today; a shared store
 * (Redis, a database) once the app runs on more than one instance.
 */
public interface UsageQuotaPort {

    /** Reserves one call. Returns false when the budget is used up. */
    boolean tryAcquire();

    int remaining();
}
