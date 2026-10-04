package dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MessagesRequest
 *
 * Body of POST /v1/messages. Deliberately has no temperature, top_p or top_k:
 * current Claude models reject them with a 400.
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-04
 */
public record MessagesRequest(
        String model,
        @JsonProperty("max_tokens") int maxTokens,
        String system,
        List<Message> messages) {

    public static MessagesRequest of(String model, int maxTokens, String system, String userMessage) {
        return new MessagesRequest(model, maxTokens, system, List.of(new Message("user", userMessage)));
    }

    public record Message(String role, String content) {
    }
}
