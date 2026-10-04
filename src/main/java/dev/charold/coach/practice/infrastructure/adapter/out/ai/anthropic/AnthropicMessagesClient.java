package dev.charold.coach.practice.infrastructure.adapter.out.ai.anthropic;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * AnthropicMessagesClient
 *
 * MicroProfile Rest Client for the Anthropic Messages API. We build the request
 * body ourselves, so only the parameters we choose are ever sent.
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-04
 */
@RegisterRestClient(configKey = "anthropic")
@RegisterProvider(AnthropicErrorMapper.class)
@Path("/v1/messages")
@ClientHeaderParam(name = "x-api-key", value = "${coach.anthropic.api-key}")
@ClientHeaderParam(name = "anthropic-version", value = "${coach.anthropic.version}")
public interface AnthropicMessagesClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    MessagesResponse create(MessagesRequest request);
}
