package sbahinho.bot.config;

import java.util.Objects;

/**
 * Configuration holder for Discord Gate bot.
 * Reads configuration from environment variables with validation.
 */
public class BotConfig {

    public static final String DISCORD_BOT_TOKEN_ENV = "DISCORD_BOT_TOKEN";
    public static final String DEFAULT_PREFIX = "!";

    private final String botToken;
    private final String prefix;

    public BotConfig(String botToken) {
        this(botToken, DEFAULT_PREFIX);
    }

    public BotConfig(String botToken, String prefix) {
        if (botToken == null || botToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Bot token cannot be null or empty.");
        }
        this.botToken = botToken.trim();
        this.prefix = (prefix == null || prefix.trim().isEmpty()) ? DEFAULT_PREFIX : prefix.trim();
    }

    /**
     * Loads configuration from the environment.
     *
     * @return configured {@link BotConfig} instance
     * @throws IllegalStateException if DISCORD_BOT_TOKEN is missing or blank
     */
    public static BotConfig fromEnvironment() {
        String token = System.getenv(DISCORD_BOT_TOKEN_ENV);
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalStateException(
                "\n" +
                "================================================================================\n" +
                " CONFIGURATION ERROR: Discord Bot Token is missing!\n" +
                "--------------------------------------------------------------------------------\n" +
                " The environment variable '" + DISCORD_BOT_TOKEN_ENV + "' is required but was not found\n" +
                " or is empty.\n" +
                "\n" +
                " Please set it before launching the application:\n" +
                "   - Windows PowerShell : $env:" + DISCORD_BOT_TOKEN_ENV + "=\"your_bot_token_here\"\n" +
                "   - Windows CMD        : set " + DISCORD_BOT_TOKEN_ENV + "=your_bot_token_here\n" +
                "   - Linux / macOS Bash : export " + DISCORD_BOT_TOKEN_ENV + "=\"your_bot_token_here\"\n" +
                "   - IntelliJ IDEA      : Run/Debug Configurations -> Environment variables\n" +
                "================================================================================"
            );
        }
        return new BotConfig(token);
    }

    public String getBotToken() {
        return botToken;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotConfig that)) return false;
        return Objects.equals(botToken, that.botToken) && Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(botToken, prefix);
    }
}
