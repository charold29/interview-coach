package dev.charold.coach.practice.infrastructure.config;

import java.time.Clock;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import dev.charold.coach.practice.application.EvaluateAnswerService;
import dev.charold.coach.practice.application.RewriteAnswerService;
import dev.charold.coach.practice.domain.port.in.EvaluateAnswerUseCase;
import dev.charold.coach.practice.domain.port.in.RewriteAnswerUseCase;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;
import dev.charold.coach.practice.domain.port.out.UsageQuotaPort;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.InterviewCoach;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.LangChain4jEvaluatorAdapter;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.mock.MockEvaluatorAdapter;
import dev.charold.coach.practice.infrastructure.adapter.out.quota.InMemoryUsageQuotaAdapter;

/**
 * PracticeBeanConfig
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
@Singleton
public class PracticeBeanConfig {

    @Inject
    @ConfigProperty(name = "coach.mock", defaultValue = "false")
    boolean mock;

    @Inject
    @ConfigProperty(name = "coach.daily-limit", defaultValue = "100")
    int dailyLimit;

    /** Resolved lazily: in mock mode the AI service is never created. */
    @Inject
    Instance<InterviewCoach> interviewCoach;

    @Produces
    @ApplicationScoped
    AnswerEvaluatorPort answerEvaluator() {
        return mock ? new MockEvaluatorAdapter() : new LangChain4jEvaluatorAdapter(interviewCoach.get());
    }

    @Produces
    @ApplicationScoped
    UsageQuotaPort usageQuota() {
        return new InMemoryUsageQuotaAdapter(dailyLimit, Clock.systemUTC());
    }

    @Produces
    @ApplicationScoped
    EvaluateAnswerUseCase evaluateAnswer(AnswerEvaluatorPort evaluator, UsageQuotaPort quota) {
        return new EvaluateAnswerService(evaluator, quota);
    }

    @Produces
    @ApplicationScoped
    RewriteAnswerUseCase rewriteAnswer(AnswerEvaluatorPort evaluator, UsageQuotaPort quota) {
        return new RewriteAnswerService(evaluator, quota);
    }
}
