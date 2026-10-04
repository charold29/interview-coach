package dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic;

import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AnthropicErrorMapper
 *
 * Turns an error response into an {@link AnthropicApiException} carrying the
 * API's error type and message. The body never contains the API key.
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-04
 */
public class AnthropicErrorMapper implements ResponseExceptionMapper<AnthropicApiException> {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final int MAX_BODY = 300;

    @Override
    public AnthropicApiException toThrowable(Response response) {
        String body;
        try {
            body = response.readEntity(String.class);
        } catch (RuntimeException unreadable) {
            body = null;
        }
        return new AnthropicApiException(response.getStatus(), describe(response.getStatus(), body));
    }

    /**
     * "HTTP 400 invalid_request_error: `top_k` is deprecated for this model." for a
     * standard Anthropic error body; the raw (truncated) body otherwise.
     */
    static String describe(int status, String body) {
        String prefix = "Anthropic API returned HTTP " + status;
        if (body == null || body.isBlank()) {
            return prefix;
        }
        try {
            JsonNode error = JSON.readTree(body).path("error");
            String type = error.path("type").asText("");
            String message = error.path("message").asText("");
            if (!message.isEmpty()) {
                return prefix + (type.isEmpty() ? "" : " " + type) + ": " + message;
            }
        } catch (Exception notJson) {
            // fall through to the raw body
        }
        String oneLine = body.replaceAll("\\s+", " ").strip();
        return prefix + ": " + (oneLine.length() <= MAX_BODY ? oneLine : oneLine.substring(0, MAX_BODY) + "...");
    }
}
