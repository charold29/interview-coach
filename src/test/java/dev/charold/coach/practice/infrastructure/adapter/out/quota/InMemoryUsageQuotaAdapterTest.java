package dev.charold.coach.practice.infrastructure.adapter.out.quota;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class InMemoryUsageQuotaAdapterTest {

    @Test
    void enforcesTheDailyLimit() {
        InMemoryUsageQuotaAdapter quota = new InMemoryUsageQuotaAdapter(2, Clock.systemUTC());

        assertTrue(quota.tryAcquire());
        assertTrue(quota.tryAcquire());
        assertFalse(quota.tryAcquire());
        assertEquals(0, quota.remaining());
    }

    @Test
    void resetsTheCounterOnANewDay() {
        MutableClock clock = new MutableClock(Instant.parse("2026-10-03T23:59:00Z"));
        InMemoryUsageQuotaAdapter quota = new InMemoryUsageQuotaAdapter(1, clock);

        assertTrue(quota.tryAcquire());
        assertFalse(quota.tryAcquire());

        clock.advance(Duration.ofMinutes(2));

        assertTrue(quota.tryAcquire());
    }

    private static final class MutableClock extends Clock {
        private Instant now;

        MutableClock(Instant start) {
            this.now = start;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }
}
