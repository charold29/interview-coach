package dev.charold.coach.practice.infrastructure.adapter.out.quota;

import java.time.Clock;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import dev.charold.coach.practice.domain.port.out.UsageQuotaPort;

/**
 * Secondary adapter: a global daily cap on evaluator calls, held in memory.
 * Good for a single instance; a restart resets it. Running several replicas
 * needs a shared store behind the same port.
 */
public class InMemoryUsageQuotaAdapter implements UsageQuotaPort {

    private final int dailyLimit;
    private final Clock clock;

    private final AtomicReference<LocalDate> day;
    private final AtomicInteger used = new AtomicInteger();

    public InMemoryUsageQuotaAdapter(int dailyLimit, Clock clock) {
        this.dailyLimit = dailyLimit;
        this.clock = clock;
        this.day = new AtomicReference<>(LocalDate.now(clock));
    }

    @Override
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

    @Override
    public int remaining() {
        return Math.max(0, dailyLimit - used.get());
    }
}
