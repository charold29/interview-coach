package dev.charold.coach.practice.infrastructure.adapter.in.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class AccessCodeGuardTest {

    @Test
    void openWhenNoAccessCodeIsConfigured() {
        AccessCodeGuard guard = new AccessCodeGuard(Optional.empty());

        assertFalse(guard.required());
        assertTrue(guard.accepts(null));
    }

    @Test
    void blankAccessCodeCountsAsNotConfigured() {
        AccessCodeGuard guard = new AccessCodeGuard(Optional.of("   "));

        assertFalse(guard.required());
    }

    @Test
    void checksTheAccessCode() {
        AccessCodeGuard guard = new AccessCodeGuard(Optional.of("friends-only"));

        assertTrue(guard.required());
        assertTrue(guard.accepts(" friends-only "));
        assertFalse(guard.accepts("wrong"));
        assertFalse(guard.accepts(null));
    }
}
