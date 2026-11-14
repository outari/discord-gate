package sbahinho.bot.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbahinho.bot.command.prefix.HelloPrefixCommand;
import sbahinho.bot.command.prefix.PingPrefixCommand;
import sbahinho.bot.command.slash.PingSlashCommand;
import sbahinho.bot.config.BotConfig;
import sbahinho.bot.listener.BotCommandListener;

import java.util.List;
import java.util.Objects;

/**
 * Service responsible for JDA initialization, gateway intents,
 * slash/prefix command registration, lifecycle management, and clean shutdown.
 */
public class DiscordBotService {

    private static final Logger logger = LoggerFactory.getLogger(DiscordBotService.class);

    private final BotConfig config;
    private final BotCommandListener commandListener;
    private JDA jda;
    private Thread shutdownHook;

    /**
     * Creates a {@link DiscordBotService} with default command handlers:
     * - Slash: /ping
     * - Prefix: !hello, !ping
     *
     * @param config bot configuration
     */
    public DiscordBotService(BotConfig config) {
        this(config, createDefaultCommandListener(config.getPrefix()));
    }

    /**
     * Creates a {@link DiscordBotService} with a custom {@link BotCommandListener}.
     *
     * @param config          bot configuration
     * @param commandListener customized command listener
     */
    public DiscordBotService(BotConfig config, BotCommandListener commandListener) {
        this.config = Objects.requireNonNull(config, "BotConfig cannot be null");
        this.commandListener = Objects.requireNonNull(commandListener, "BotCommandListener cannot be null");
    }

    private static BotCommandListener createDefaultCommandListener(String prefix) {
        BotCommandListener listener = new BotCommandListener(prefix);
        // Register default slash commands
        listener.registerSlashCommand(new PingSlashCommand());

        // Register default prefix commands
        listener.registerPrefixCommand(new HelloPrefixCommand());
        listener.registerPrefixCommand(new PingPrefixCommand());
        return listener;
    }

    /**
     * Initializes JDA, configures gateway intents, registers event listeners,
     * waits until connection reaches ready state, and registers slash commands.
     *
     * @throws InterruptedException if interrupted while waiting for JDA ready state
     */
    public synchronized void start() throws InterruptedException {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            logger.warn("DiscordBotService is already running.");
            return;
        }

        logger.info("Initializing Discord bot client...");

        JDABuilder builder = JDABuilder.createDefault(config.getBotToken())
                // Enable intents required for prefix commands and message monitoring
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                // Set bot status activity
                .setActivity(Activity.playing("/ping | " + config.getPrefix() + "hello"))
                // Register our command dispatcher
                .addEventListeners(commandListener);

        // Build JDA client and block until ready
        logger.info("Connecting to Discord Gateway and awaiting ready state...");
        this.jda = builder.build().awaitReady();
        logger.info("Connected successfully as: {}", jda.getSelfUser().getAsTag());

        // Register global slash commands
        registerSlashCommands();

        // Register JVM shutdown hook for graceful termination
        registerShutdownHook();
    }

    private void registerSlashCommands() {
        List<SlashCommandData> slashCommands = commandListener.getSlashCommandDataList();
        if (slashCommands.isEmpty()) {
            logger.info("No slash commands to register.");
            return;
        }

        logger.info("Registering {} slash command(s) with Discord...", slashCommands.size());
        jda.updateCommands()
                .addCommands(slashCommands)
                .queue(
                        commands -> logger.info("Successfully published {} global slash command(s) to Discord: {}",
                                commands.size(), commands.stream().map(net.dv8tion.jda.api.interactions.commands.Command::getName).toList()),
                        error -> logger.error("Failed to register slash commands with Discord: {}", error.getMessage(), error)
                );
    }

    private void registerShutdownHook() {
        if (shutdownHook != null) {
            return;
        }
        shutdownHook = new Thread(() -> {
            logger.info("Shutdown hook invoked. Terminating Discord bot session...");
            stop();
        }, "discord-bot-shutdown-hook");
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    /**
     * Gracefully stops the bot and disconnects from the Discord gateway.
     */
    public synchronized void stop() {
        if (jda != null) {
            logger.info("Shutting down JDA client...");
            jda.shutdown();
            this.jda = null;
            logger.info("Discord bot disconnected.");
        }
    }

    public JDA getJDA() {
        return jda;
    }

    public BotConfig getConfig() {
        return config;
    }

    public BotCommandListener getCommandListener() {
        return commandListener;
    }
}
