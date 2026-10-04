package dev.charold.coach.practice.infrastructure.adapter.out.ai;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;
import dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.AnthropicMessagesClient;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.MessagesRequest;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.MessagesResponse;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.dto.AiEvaluationDto;

/**
 * AnthropicEvaluatorAdapter
 *
 * Secondary adapter that calls the Anthropic Messages API directly through a
 * MicroProfile Rest Client. Same prompts and same DTO contract as the
 * LangChain4j adapter, but we own the request body, so no sampling parameter
 * the current models reject (temperature, top_p, top_k) is ever sent.
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-04
 */
public class AnthropicEvaluatorAdapter implements AnswerEvaluatorPort {

    private final AnthropicMessagesClient client;
    private final ObjectMapper json;
    private final String model;
    private final int maxTokens;

    public AnthropicEvaluatorAdapter(AnthropicMessagesClient client, String model, int maxTokens) {
        this.client = Objects.requireNonNull(client);
        this.model = Objects.requireNonNull(model);
        this.maxTokens = maxTokens;
        this.json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Evaluation evaluate(PracticeProfile profile, String question, String answer) {
        MessagesResponse response = client.create(MessagesRequest.of(model, maxTokens,
                AiPrompts.RUBRIC + "\n" + AiPrompts.JSON_FORMAT,
                AiPrompts.evaluationRequest(profile, question, answer)));
        if (response.truncated()) {
            throw new IllegalStateException("The model's evaluation was cut off at " + maxTokens
                    + " tokens; raise coach.anthropic.max-tokens");
        }
        return AiEvaluationMapper.toDomain(parse(response.text()));
    }

    @Override
    public String rewrite(PracticeProfile profile, String question, String currentAnswer, RewriteDirection direction) {
        MessagesResponse response = client.create(MessagesRequest.of(model, maxTokens,
                AiPrompts.REWRITE_SYSTEM,
                AiPrompts.rewriteRequest(profile, question, currentAnswer,
                        LangChain4jEvaluatorAdapter.prompt(direction))));
        return response.text().strip();
    }

    /** Reads the JSON object out of the reply, tolerating stray markdown fences or prose around it. */
    AiEvaluationDto parse(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalStateException("The model did not return a JSON evaluation: " + preview(text));
        }
        try {
            return json.readValue(text.substring(start, end + 1), AiEvaluationDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("The model returned malformed JSON: " + e.getOriginalMessage(), e);
        }
    }

    private static String preview(String text) {
        String oneLine = text.replaceAll("\\s+", " ").strip();
        return oneLine.length() <= 120 ? oneLine : oneLine.substring(0, 120) + "...";
    }
}
