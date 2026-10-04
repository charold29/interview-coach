package dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.MessagesResponse.ContentBlock;

/**
 * AnthropicErrorMapperTest
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-04
 */
class AnthropicErrorMapperTest {

    @Test
    void extractsTypeAndMessageFromAStandardErrorBody() {
        String body = """
                {"type":"error","error":{"type":"invalid_request_error",
                 "message":"`top_k` is deprecated for this model."},"request_id":"req_1"}
                """;

        assertEquals("Anthropic API returned HTTP 400 invalid_request_error: `top_k` is deprecated for this model.",
                AnthropicErrorMapper.describe(400, body));
    }

    @Test
    void fallsBackToTheRawBodyWhenItIsNotJson() {
        assertEquals("Anthropic API returned HTTP 502: Bad gateway",
                AnthropicErrorMapper.describe(502, "Bad\n  gateway"));
    }

    @Test
    void truncatesLongBodies() {
        String message = AnthropicErrorMapper.describe(500, "x".repeat(1_000));

        assertTrue(message.endsWith("..."), message);
        assertTrue(message.length() < 400, "length " + message.length());
    }

    @Test
    void worksWithoutABody() {
        assertEquals("Anthropic API returned HTTP 529", AnthropicErrorMapper.describe(529, null));
    }

    @Test
    void responseTextJoinsOnlyTextBlocks() {
        MessagesResponse response = new MessagesResponse(
                Arrays.asList(new ContentBlock("thinking", "ignored"), null,
                        new ContentBlock("text", "Hello "), new ContentBlock("text", "there")),
                "end_turn");

        assertEquals("Hello there", response.text());
        assertEquals("", new MessagesResponse(null, null).text());
        assertTrue(new MessagesResponse(List.of(), "max_tokens").truncated());
    }
}
