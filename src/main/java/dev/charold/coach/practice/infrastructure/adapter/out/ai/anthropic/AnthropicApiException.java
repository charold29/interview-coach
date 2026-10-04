package dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic;

/**
 * AnthropicApiException
 *
 * An error response from the Anthropic API, with the API's own message, so one
 * log line says what went wrong (e.g. "HTTP 400 invalid_request_error: ...").
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-04
 */
public class AnthropicApiException extends RuntimeException {

    private final int status;

    public AnthropicApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
