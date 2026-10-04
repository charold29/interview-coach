package dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MessagesResponse
 *
 * The parts of the Messages API response we use. Everything else is ignored.
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-04
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessagesResponse(
        List<ContentBlock> content,
        @JsonProperty("stop_reason") String stopReason) {

    /** Concatenated text of all text blocks; empty when there are none. */
    public String text() {
        if (content == null) {
            return "";
        }
        return content.stream()
                .filter(block -> block != null && "text".equals(block.type()) && block.text() != null)
                .map(ContentBlock::text)
                .collect(Collectors.joining());
    }

    /** True when the model stopped because it hit max_tokens, so the text is cut off. */
    public boolean truncated() {
        return "max_tokens".equals(stopReason);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentBlock(String type, String text) {
    }
}
