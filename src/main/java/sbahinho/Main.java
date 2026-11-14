package sbahinho;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbahinho.bot.config.BotConfig;
import sbahinho.bot.service.DiscordBotService;

/**
 * Main entry point for the discord-gate application.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting Discord Gate bot application...");

            // Read and validate bot configuration from environment
            BotConfig config = BotConfig.fromEnvironment();

            // Create and initialize the Discord bot service
            DiscordBotService botService = new DiscordBotService(config);
            botService.start();

            logger.info("Bot application is fully initialized and listening for interactions.");
        } catch (IllegalStateException ex) {
            // Missing token configuration error
            logger.error("Startup aborted due to configuration error: {}", ex.getMessage());
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (net.dv8tion.jda.api.exceptions.InvalidTokenException ex) {
            logger.error("Authentication failed: The provided DISCORD_BOT_TOKEN is invalid. Please verify your token in the Discord Developer Portal.");
            System.exit(1);
        } catch (InterruptedException ex) {
            logger.error("Bot startup was interrupted: {}", ex.getMessage(), ex);
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (Exception ex) {
            logger.error("Fatal error occurred during bot execution: {}", ex.getMessage(), ex);
            System.exit(1);
        }
    }
}
