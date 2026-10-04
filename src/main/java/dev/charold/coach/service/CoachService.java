package dev.charold.coach.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import dev.charold.coach.ai.InterviewCoach;
import dev.charold.coach.domain.EnglishFix;
import dev.charold.coach.domain.Evaluation;
import dev.charold.coach.domain.PracticeProfile;

/**
 * Entry point for the web layer. Applies the usage guard and switches to a
 * canned response in mock mode, so the UI can be developed and tested without
 * an API key or spending tokens.
 */
@ApplicationScoped
public class CoachService {

    public enum Direction {
        SHORTER("shorter"),
        LONGER("longer");

        private final String prompt;

        Direction(String prompt) {
            this.prompt = prompt;
        }

        public static Direction parse(String value) {
            return "longer".equalsIgnoreCase(value) ? LONGER : SHORTER;
        }
    }

    private final Instance<InterviewCoach> coach;
    private final UsageGuard guard;
    private final boolean mock;

    @Inject
    public CoachService(
            Instance<InterviewCoach> coach,
            UsageGuard guard,
            @ConfigProperty(name = "coach.mock", defaultValue = "false") boolean mock) {
        this.coach = coach;
        this.guard = guard;
        this.mock = mock;
    }

    public Evaluation evaluate(PracticeProfile profile, String question, String answer) {
        reserveCall();
        if (mock) {
            return MockData.evaluation();
        }
        return coach.get().evaluate(profile, question, answer);
    }

    public String rewrite(PracticeProfile profile, String question, String currentAnswer, Direction direction) {
        reserveCall();
        if (mock) {
            return direction == Direction.SHORTER ? MockData.SHORT_ANSWER : MockData.LONG_ANSWER;
        }
        return coach.get().rewrite(profile, question, currentAnswer, direction.prompt).strip();
    }

    public boolean isMock() {
        return mock;
    }

    private void reserveCall() {
        if (!guard.tryAcquire()) {
            throw new LimitReachedException();
        }
    }

    public static class LimitReachedException extends RuntimeException {
        public LimitReachedException() {
            super("Daily practice limit reached");
        }
    }

    /** Canned data for mock mode. Realistic enough to design the UI against. */
    static final class MockData {

        static final String SHORT_ANSWER = "Mock mode: a cache-aside cache cuts read latency from tens of "
                + "milliseconds to about one, at the cost of serving data that can be stale until the TTL expires.";

        static final String LONG_ANSWER = "Mock mode: I'd put a cache-aside layer in front of the database. "
                + "On a hit we serve in about a millisecond instead of the 20 to 50 we pay on the database. "
                + "On a miss we read from the source of truth and populate the cache with a TTL. "
                + "The trade-off is staleness, so for balances I'd keep the TTL short or invalidate on write. "
                + "The failure mode to watch is a thundering herd when a hot key expires, which I'd handle "
                + "with request coalescing. I'd track hit ratio and p99 latency to know it's working.";

        private MockData() {
        }

        static Evaluation evaluation() {
            return new Evaluation(
                    false, 7, 5, 6, 6,
                    "Mock mode: correct but stays at the definition level; no numbers or failure modes.",
                    List.of(
                            "You led with the purpose: \"the cache makes the reads faster\".",
                            "You named the invalidation problem: \"the data can be old\"."),
                    List.of(
                            "No order of magnitude: cache hit around 1 ms vs 20-50 ms on the database.",
                            "No pattern name: cache-aside vs write-through.",
                            "No failure mode: thundering herd / cache stampede when a hot key expires.",
                            "No metric: hit ratio and p99 latency."),
                    LONG_ANSWER,
                    List.of(
                            new EnglishFix("the data can be old", "the data can be stale",
                                    "\"stale\" is the standard term for outdated cached data."),
                            new EnglishFix("depends of the TTL", "depends on the TTL",
                                    "\"depend\" always takes \"on\"."),
                            new EnglishFix("actually we use Redis", "currently we use Redis",
                                    "\"actually\" means \"in fact\", not \"currently\".")),
                    "How would you invalidate the cache when a balance changes?");
        }
    }
}
