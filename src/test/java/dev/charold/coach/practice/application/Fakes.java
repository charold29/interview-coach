package dev.charold.coach.practice.application;

import java.util.List;

import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;
import dev.charold.coach.practice.domain.port.out.UsageQuotaPort;

/**
 * Hand-written fakes for the driven ports. Plain Java on purpose: testing a
 * use case needs no container and no mocking library.
 */
final class Fakes {

    private Fakes() {
    }

    static final class RecordingEvaluator implements AnswerEvaluatorPort {
        static final Evaluation RESULT = new Evaluation(false, 8, 7, 7, 8, "verdict",
                List.of(), List.of(), "model answer", List.of(), "follow-up");

        int evaluateCalls;
        int rewriteCalls;
        String lastQuestion;
        RewriteDirection lastDirection;

        @Override
        public Evaluation evaluate(PracticeProfile profile, String question, String answer) {
            evaluateCalls++;
            lastQuestion = question;
            return RESULT;
        }

        @Override
        public String rewrite(PracticeProfile profile, String question, String currentAnswer,
                RewriteDirection direction) {
            rewriteCalls++;
            lastQuestion = question;
            lastDirection = direction;
            return "rewritten " + direction;
        }
    }

    static final class FixedQuota implements UsageQuotaPort {
        private int remaining;

        FixedQuota(int remaining) {
            this.remaining = remaining;
        }

        @Override
        public boolean tryAcquire() {
            if (remaining == 0) {
                return false;
            }
            remaining--;
            return true;
        }

        @Override
        public int remaining() {
            return remaining;
        }
    }

    static PracticeProfile profile() {
        return new PracticeProfile("Backend Engineer", null, 6, "Java", "Español");
    }
}
