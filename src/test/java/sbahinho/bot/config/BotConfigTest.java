package sbahinho.bot.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotConfigTest {

    @Test
    @DisplayName("Should create BotConfig with valid token and default prefix")
    void testValidTokenDefaultPrefix() {
        BotConfig config = new BotConfig("valid-sample-token-12345");
        assertEquals("valid-sample-token-12345", config.getBotToken());
        assertEquals("!", config.getPrefix());
    }

    @Test
    @DisplayName("Should trim token and support custom prefix")
    void testCustomPrefixAndTrim() {
        BotConfig config = new BotConfig("  trimmed-token  ", "?");
        assertEquals("trimmed-token", config.getBotToken());
        assertEquals("?", config.getPrefix());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when token is null")
    void testNullTokenThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new BotConfig(null));
        assertTrue(ex.getMessage().contains("cannot be null or empty"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when token is blank")
    void testBlankTokenThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new BotConfig("   "));
        assertTrue(ex.getMessage().contains("cannot be null or empty"));
    }

    @Test
    @DisplayName("fromEnvironment should fail when DISCORD_BOT_TOKEN is missing or blank")
    void testFromEnvironmentWithoutToken() {
        // If DISCORD_BOT_TOKEN is not set in test environment, fromEnvironment throws IllegalStateException
        String token = System.getenv(BotConfig.DISCORD_BOT_TOKEN_ENV);
        if (token == null || token.trim().isEmpty()) {
            IllegalStateException ex = assertThrows(IllegalStateException.class, BotConfig::fromEnvironment);
            assertTrue(ex.getMessage().contains(BotConfig.DISCORD_BOT_TOKEN_ENV));
            assertTrue(ex.getMessage().contains("CONFIGURATION ERROR"));
        }
    }
}
