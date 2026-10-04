package dev.charold.coach.practice.infrastructure.adapter.out.ai.mock;

import java.util.List;

import dev.charold.coach.practice.domain.model.EnglishFix;
import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;

/**
 * Secondary adapter for mock mode: canned responses, no API calls. Realistic
 * enough to design the UI against and to run the tests without a key.
 */
public class MockEvaluatorAdapter implements AnswerEvaluatorPort {

    static final String SHORT_ANSWER = "Mock mode: a cache-aside cache cuts read latency from tens of "
            + "milliseconds to about one, at the cost of serving data that can be stale until the TTL expires.";

    static final String LONG_ANSWER = "Mock mode: I'd put a cache-aside layer in front of the database. "
            + "On a hit we serve in about a millisecond instead of the 20 to 50 we pay on the database. "
            + "On a miss we read from the source of truth and populate the cache with a TTL. "
            + "The trade-off is staleness, so for balances I'd keep the TTL short or invalidate on write. "
            + "The failure mode to watch is a thundering herd when a hot key expires, which I'd handle "
            + "with request coalescing. I'd track hit ratio and p99 latency to know it's working.";

    @Override
    public Evaluation evaluate(PracticeProfile profile, String question, String answer) {
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

    @Override
    public String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction) {
        return direction == RewriteDirection.SHORTER ? SHORT_ANSWER : LONG_ANSWER;
    }
}
