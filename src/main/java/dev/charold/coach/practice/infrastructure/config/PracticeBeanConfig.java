package dev.charold.coach.practice.infrastructure.config;

import java.time.Clock;
import java.util.Locale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import dev.charold.coach.practice.application.EvaluateAnswerService;
import dev.charold.coach.practice.application.RewriteAnswerService;
import dev.charold.coach.practice.domain.port.in.EvaluateAnswerUseCase;
import dev.charold.coach.practice.domain.port.in.RewriteAnswerUseCase;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;
import dev.charold.coach.practice.domain.port.out.UsageQuotaPort;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.AnthropicEvaluatorAdapter;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.InterviewCoach;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.LangChain4jEvaluatorAdapter;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.AnthropicMessagesClient;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.mock.MockEvaluatorAdapter;
import dev.charold.coach.practice.infrastructure.adapter.out.quota.InMemoryUsageQuotaAdapter;

/**
 * PracticeBeanConfig
 *
 * The only place that knows which adapter backs each port. coach.mock wins;
 * otherwise coach.provider picks the evaluator: "anthropic" (direct Messages
 * API, the default) or "langchain4j".
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
    @ConfigProperty(name = "coach.provider", defaultValue = "anthropic")
    String provider;

    @Inject
    @ConfigProperty(name = "coach.anthropic.model")
    String anthropicModel;

    @Inject
    @ConfigProperty(name = "coach.anthropic.max-tokens", defaultValue = "2048")
    int anthropicMaxTokens;

    @Inject
    @ConfigProperty(name = "coach.daily-limit", defaultValue = "100")
    int dailyLimit;

    /** Resolved lazily: only the selected evaluator's client is ever created. */
    @Inject
    Instance<InterviewCoach> interviewCoach;

    @Inject
    @RestClient
    Instance<AnthropicMessagesClient> anthropicClient;

    @Produces
    @ApplicationScoped
    AnswerEvaluatorPort answerEvaluator() {
        if (mock) {
            return new MockEvaluatorAdapter();
        }
        return switch (provider.strip().toLowerCase(Locale.ROOT)) {
            case "anthropic" -> new AnthropicEvaluatorAdapter(anthropicClient.get(), anthropicModel, anthropicMaxTokens);
            case "langchain4j" -> new LangChain4jEvaluatorAdapter(interviewCoach.get());
            default -> throw new IllegalStateException(
                    "Unknown coach.provider '" + provider + "'. Use 'anthropic' or 'langchain4j'.");
        };
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
