package dev.charold.coach.practice.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RewriteDirectionTest {

    @Test
    void parsesFormValues() {
        assertEquals(RewriteDirection.LONGER, RewriteDirection.parse(" Longer "));
        assertEquals(RewriteDirection.SHORTER, RewriteDirection.parse("shorter"));
        assertEquals(RewriteDirection.SHORTER, RewriteDirection.parse(null));
        assertEquals(RewriteDirection.SHORTER, RewriteDirection.parse("sideways"));
    }
}
