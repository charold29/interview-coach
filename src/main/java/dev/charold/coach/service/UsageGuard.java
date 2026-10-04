package dev.charold.coach.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Protects the API key when the app is shared: an optional access code and a
 * global daily cap on model calls. In memory on purpose: one instance, no
 * database. A restart resets the counter, which is acceptable for a side project.
 */
@ApplicationScoped
public class UsageGuard {

    private final Optional<String> accessCode;
    private final int dailyLimit;
    private final Clock clock;

    private final AtomicReference<LocalDate> day;
    private final AtomicInteger used = new AtomicInteger();

    @Inject
    public UsageGuard(
            @ConfigProperty(name = "coach.access-code") Optional<String> accessCode,
            @ConfigProperty(name = "coach.daily-limit", defaultValue = "100") int dailyLimit) {
        this(accessCode, dailyLimit, Clock.systemUTC());
    }

    UsageGuard(Optional<String> accessCode, int dailyLimit, Clock clock) {
        this.accessCode = accessCode.map(String::strip).filter(code -> !code.isEmpty());
        this.dailyLimit = dailyLimit;
        this.clock = clock;
        this.day = new AtomicReference<>(LocalDate.now(clock));
    }

    public boolean accessCodeRequired() {
        return accessCode.isPresent();
    }

    public boolean accepts(String providedCode) {
        return accessCode.map(code -> code.equals(providedCode == null ? "" : providedCode.strip()))
                .orElse(true);
    }

    /** Reserves one model call for today. Returns false when the daily cap is reached. */
    public boolean tryAcquire() {
        LocalDate today = LocalDate.now(clock);
        LocalDate current = day.get();
        if (!today.equals(current) && day.compareAndSet(current, today)) {
            used.set(0);
        }
        if (used.incrementAndGet() > dailyLimit) {
            used.decrementAndGet();
            return false;
        }
        return true;
    }

    public int remainingToday() {
        return Math.max(0, dailyLimit - used.get());
    }
}
