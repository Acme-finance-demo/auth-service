package com.example.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class TokenTemplateTest {

    @Test
    void substitutesEveryPlaceholder() {
        TokenTemplate template = new TokenTemplate("a=${one} b=${two}");
        assertEquals("a=1 b=2", template.render(Map.of("one", "1", "two", "2")));
    }

    @Test
    void leavesAnUnknownPlaceholderVisible() {
        TokenTemplate template = new TokenTemplate("a=${one} b=${missing}");
        assertEquals("a=1 b=${missing}", template.render(Map.of("one", "1")));
    }

    @Test
    void doesNotResolveLookupsInTheValues() {
        // The substitutor is map-backed, so a value that looks like a lookup stays a string.
        TokenTemplate template = new TokenTemplate("v=${one}");
        String rendered = template.render(Map.of("one", "${script:js:1+1}"));
        assertTrue(rendered.contains("script:js"), rendered);
    }

    @Test
    void refusesABlankTemplate() {
        assertThrows(IllegalArgumentException.class, () -> new TokenTemplate("  "));
    }
}
