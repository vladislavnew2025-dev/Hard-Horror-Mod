package com.hardhorror;

/**
 * Tiny low-intensity screamer frame for testing.
 */
public final class PixelScreamer {
    private PixelScreamer() {
    }

    public static String frame() {
        return """
            +----------+
            |  .    .  |
            |    --    |
            |  \\____/  |
            +----------+
            """;
    }
}
