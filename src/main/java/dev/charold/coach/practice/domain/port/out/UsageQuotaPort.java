package dev.charold.coach.practice.domain.port.out;

/**
 * UsageQuotaPort
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
public interface UsageQuotaPort {

    /** Reserves one call. Returns false when the budget is used up. */
    boolean tryAcquire();

    int remaining();
}
