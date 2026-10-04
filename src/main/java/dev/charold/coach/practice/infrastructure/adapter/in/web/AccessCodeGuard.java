package dev.charold.coach.practice.infrastructure.adapter.in.web;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Optional shared code required to use a deployed instance. An HTTP-level
 * concern that protects the API key, not a business rule, so it lives in the
 * web adapter instead of the domain.
 */
@ApplicationScoped
public class AccessCodeGuard {

    private final Optional<String> accessCode;

    @Inject
    public AccessCodeGuard(@ConfigProperty(name = "coach.access-code") Optional<String> accessCode) {
        this.accessCode = accessCode.map(String::strip).filter(code -> !code.isEmpty());
    }

    public boolean required() {
        return accessCode.isPresent();
    }

    public boolean accepts(String providedCode) {
        return accessCode.map(code -> code.equals(providedCode == null ? "" : providedCode.strip()))
                .orElse(true);
    }
}
