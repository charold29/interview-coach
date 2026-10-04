package dev.charold.coach.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class UsageGuardTest {

    @Test
    void openWhenNoAccessCodeIsConfigured() {
        UsageGuard guard = new UsageGuard(Optional.empty(), 10, Clock.systemUTC());

        assertFalse(guard.accessCodeRequired());
        assertTrue(guard.accepts(null));
    }

    @Test
    void blankAccessCodeCountsAsNotConfigured() {
        UsageGuard guard = new UsageGuard(Optional.of("   "), 10, Clock.systemUTC());

        assertFalse(guard.accessCodeRequired());
    }

    @Test
    void checksTheAccessCode() {
        UsageGuard guard = new UsageGuard(Optional.of("friends-only"), 10, Clock.systemUTC());

        assertTrue(guard.accessCodeRequired());
        assertTrue(guard.accepts(" friends-only "));
        assertFalse(guard.accepts("wrong"));
        assertFalse(guard.accepts(null));
    }

    @Test
    void enforcesTheDailyLimit() {
        UsageGuard guard = new UsageGuard(Optional.empty(), 2, Clock.systemUTC());

        assertTrue(guard.tryAcquire());
        assertTrue(guard.tryAcquire());
        assertFalse(guard.tryAcquire());
        assertEquals(0, guard.remainingToday());
    }

    @Test
    void resetsTheCounterOnANewDay() {
        MutableClock clock = new MutableClock(Instant.parse("2026-10-03T23:59:00Z"));
        UsageGuard guard = new UsageGuard(Optional.empty(), 1, clock);

        assertTrue(guard.tryAcquire());
        assertFalse(guard.tryAcquire());

        clock.advance(Duration.ofMinutes(2));

        assertTrue(guard.tryAcquire());
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
