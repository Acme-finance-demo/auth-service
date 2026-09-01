package com.example.auth;

import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

/**
 * Renders the audit lines this service emits.
 *
 * <p>The substitutor is built from an explicit map. {@link StringSubstitutor#createInterpolator()}
 * is deliberately not used: the interpolating variant resolves {@code ${script:…}},
 * {@code ${dns:…}} and {@code ${url:…}} lookups, which is not something an audit template
 * should be able to do.
 */
public final class TokenTemplate {

    private final String template;

    public TokenTemplate(String template) {
        if (template == null || template.isBlank()) {
            throw new IllegalArgumentException("template is required");
        }
        this.template = template;
    }

    public String render(Map<String, String> values) {
        StringSubstitutor substitutor = new StringSubstitutor(values);
        // Leave an unresolved placeholder visible rather than silently dropping it,
        // so a broken template shows up in the audit trail instead of hiding.
        substitutor.setEnableUndefinedVariableException(false);
        return substitutor.replace(template);
    }

    public String template() {
        return template;
    }
}
