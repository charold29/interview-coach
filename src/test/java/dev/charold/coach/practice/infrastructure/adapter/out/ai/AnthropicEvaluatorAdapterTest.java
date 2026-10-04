package dev.charold.coach.practice.infrastructure.adapter.out.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.enterprise.inject.Vetoed;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.model.RewriteDirection;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.AnthropicMessagesClient;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.MessagesRequest;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.MessagesResponse;
import dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic.MessagesResponse.ContentBlock;

/**
 * AnthropicEvaluatorAdapterTest
 *
 * The REST client is an interface, so a recording stub stands in for the API.
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-04
 */
class AnthropicEvaluatorAdapterTest {

    private static final PracticeProfile PROFILE =
            new PracticeProfile("Backend Engineer", null, 6, "Java, Quarkus", "Español");

    private static final String EVALUATION_JSON = """
            {"technicallyIncorrect": false, "technicalAccuracy": 8, "depth": 6, "terminology": 7,
             "clarity": 7, "verdict": "Solid.", "didWell": ["named the pattern"],
             "missing": ["no p99"], "modelAnswer": "Senior answer.",
             "englishFixes": [{"youSaid": "depends of", "better": "depends on", "why": "preposición"}],
             "followUp": "And when the cache is down?", "extraFieldTheModelInvented": 1}
            """;

    @Test
    void requestBodyCarriesNoSamplingParameters() throws Exception {
        RecordingClient client = new RecordingClient(EVALUATION_JSON, "end_turn");
        new AnthropicEvaluatorAdapter(client, "claude-sonnet-5-5", 2048).evaluate(PROFILE, "q", "a");

        JsonNode body = new ObjectMapper().valueToTree(client.lastRequest);

        Set<String> fields = new TreeSet<>();
        body.fieldNames().forEachRemaining(fields::add);
        assertEquals(Set.of("max_tokens", "messages", "model", "system"), fields,
                "current Claude models reject temperature, top_p and top_k with a 400");
        assertEquals("claude-sonnet-5-5", body.get("model").asText());
        assertEquals(2048, body.get("max_tokens").asInt());
    }

    @Test
    void evaluationPromptCarriesTheRubricTheProfileAndTheAnswer() {
        RecordingClient client = new RecordingClient(EVALUATION_JSON, "end_turn");
        new AnthropicEvaluatorAdapter(client, "m", 2048).evaluate(PROFILE, "Why cache?", "Because 50% faster.");

        assertTrue(client.lastRequest.system().startsWith(AiPrompts.RUBRIC));
        assertTrue(client.lastRequest.system().contains("single JSON object"));
        String user = client.lastRequest.messages().get(0).content();
        assertTrue(user.contains("Years of experience: 6"), user);
        assertTrue(user.contains("Feedback language for englishFixes.why: Español"), user);
        assertTrue(user.contains("Because 50% faster."), "percent signs in answers must survive formatting");
    }

    @Test
    void mapsTheJsonReplyToTheDomain() {
        Evaluation evaluation = new AnthropicEvaluatorAdapter(new RecordingClient(EVALUATION_JSON, "end_turn"),
                "m", 2048).evaluate(PROFILE, "q", "a");

        assertEquals("7.0", evaluation.overallDisplay());
        assertEquals("depends on", evaluation.englishFixes().get(0).better());
        assertEquals("And when the cache is down?", evaluation.followUp());
        assertFalse(evaluation.technicallyIncorrect());
    }

    @Test
    void toleratesMarkdownFencesAroundTheJson() {
        String fenced = "Here you go:\n```json\n" + EVALUATION_JSON + "\n```";

        Evaluation evaluation = new AnthropicEvaluatorAdapter(new RecordingClient(fenced, "end_turn"), "m", 2048)
                .evaluate(PROFILE, "q", "a");

        assertEquals(8, evaluation.technicalAccuracy());
    }

    @Test
    void failsClearlyWhenTheReplyIsCutOff() {
        AnthropicEvaluatorAdapter adapter =
                new AnthropicEvaluatorAdapter(new RecordingClient("{\"technical", "max_tokens"), "m", 2048);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> adapter.evaluate(PROFILE, "q", "a"));

        assertTrue(e.getMessage().contains("max-tokens"), e.getMessage());
    }

    @Test
    void failsClearlyWhenTheReplyIsNotJson() {
        AnthropicEvaluatorAdapter adapter =
                new AnthropicEvaluatorAdapter(new RecordingClient("I can't evaluate that.", "end_turn"), "m", 2048);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> adapter.evaluate(PROFILE, "q", "a"));

        assertTrue(e.getMessage().contains("did not return a JSON evaluation"), e.getMessage());
    }

    @Test
    void rewriteSendsTheDirectionAndTrimsTheReply() {
        RecordingClient client = new RecordingClient("  Shorter answer.\n", "end_turn");

        String result = new AnthropicEvaluatorAdapter(client, "m", 2048)
                .rewrite(PROFILE, "q", "long answer", RewriteDirection.SHORTER);

        assertEquals("Shorter answer.", result);
        assertEquals(AiPrompts.REWRITE_SYSTEM, client.lastRequest.system());
        assertTrue(client.lastRequest.messages().get(0).content().contains("Rewrite it to be shorter."));
    }

    /**
     * Quarkus turns every implementation of a REST client interface into a CDI
     * bean, test classes included; @Vetoed keeps this stub out of the container.
     */
    @Vetoed
    private static final class RecordingClient implements AnthropicMessagesClient {
        private final String replyText;
        private final String stopReason;
        MessagesRequest lastRequest;

        RecordingClient(String replyText, String stopReason) {
            this.replyText = replyText;
            this.stopReason = stopReason;
        }

        @Override
        public MessagesResponse create(MessagesRequest request) {
            lastRequest = request;
            return new MessagesResponse(List.of(new ContentBlock("text", replyText)), stopReason);
        }
    }
}
