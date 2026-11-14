package sbahinho.bot.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbahinho.bot.command.PrefixCommand;
import sbahinho.bot.command.SlashCommand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scalable event listener extending {@link ListenerAdapter}.
 * Dispatches slash commands and prefix-based message commands to registered handlers.
 */
public class BotCommandListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BotCommandListener.class);

    private final String prefix;
    private final Map<String, SlashCommand> slashCommands = new ConcurrentHashMap<>();
    private final Map<String, PrefixCommand> prefixCommands = new ConcurrentHashMap<>();

    public BotCommandListener(String prefix) {
        this.prefix = (prefix == null || prefix.trim().isEmpty()) ? "!" : prefix.trim();
    }

    /**
     * Registers a new slash command handler.
     *
     * @param command the {@link SlashCommand} to register
     * @return this listener instance for fluent chaining
     */
    public BotCommandListener registerSlashCommand(SlashCommand command) {
        Objects.requireNonNull(command, "SlashCommand cannot be null");
        String name = command.getCommandData().getName().toLowerCase();
        slashCommands.put(name, command);
        logger.debug("Registered slash command: /{}", name);
        return this;
    }

    /**
     * Registers a new prefix-based command handler.
     *
     * @param command the {@link PrefixCommand} to register
     * @return this listener instance for fluent chaining
     */
    public BotCommandListener registerPrefixCommand(PrefixCommand command) {
        Objects.requireNonNull(command, "PrefixCommand cannot be null");
        String name = command.getName().toLowerCase();
        prefixCommands.put(name, command);
        logger.debug("Registered prefix command: {}{}", prefix, name);
        return this;
    }

    /**
     * Collects all {@link SlashCommandData} for global or guild registration with Discord.
     *
     * @return unmodifiable list of {@link SlashCommandData}
     */
    public List<SlashCommandData> getSlashCommandDataList() {
        return slashCommands.values().stream()
                .map(SlashCommand::getCommandData)
                .toList();
    }

    public Map<String, SlashCommand> getSlashCommands() {
        return Collections.unmodifiableMap(slashCommands);
    }

    public Map<String, PrefixCommand> getPrefixCommands() {
        return Collections.unmodifiableMap(prefixCommands);
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("=================================================");
        logger.info(" Bot is READY!");
        logger.info(" Logged in as: {}", event.getJDA().getSelfUser().getAsTag());
        logger.info(" Bot ID:       {}", event.getJDA().getSelfUser().getId());
        logger.info(" Connected to: {} guild(s)", event.getGuildTotalCount());
        logger.info(" Gateway ping: {} ms", event.getJDA().getGatewayPing());
        logger.info(" Slash commands registered locally: {}", slashCommands.keySet());
        logger.info(" Prefix commands registered:        {}", prefixCommands.keySet());
        logger.info(" Command prefix: '{}'", prefix);
        logger.info("=================================================");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName().toLowerCase();
        SlashCommand command = slashCommands.get(commandName);

        if (command == null) {
            logger.warn("Received unrecognized slash command: /{}", commandName);
            event.reply("Unknown slash command: /" + commandName)
                    .setEphemeral(true)
                    .queue();
            return;
        }

        try {
            logger.info("Executing slash command '/{}' invoked by user '{}' ({})",
                    commandName, event.getUser().getAsTag(), event.getUser().getId());
            command.execute(event);
        } catch (Exception ex) {
            logger.error("Error executing slash command '/{}': {}", commandName, ex.getMessage(), ex);
            String errorMessage = "⚠️ An error occurred while executing the slash command.";
            if (event.isAcknowledged()) {
                event.getHook().sendMessage(errorMessage).setEphemeral(true).queue();
            } else {
                event.reply(errorMessage).setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore messages sent by bots or webhooks
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        String rawContent = event.getMessage().getContentRaw();
        if (rawContent == null || !rawContent.startsWith(prefix)) {
            return;
        }

        String contentWithoutPrefix = rawContent.substring(prefix.length()).trim();
        if (contentWithoutPrefix.isEmpty()) {
            return;
        }

        String[] parts = contentWithoutPrefix.split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        PrefixCommand command = prefixCommands.get(commandName);
        if (command == null) {
            // Silently ignore unrecognized commands or unknown prefixes to prevent noise
            logger.debug("No prefix handler registered for command: {}{}", prefix, commandName);
            return;
        }

        try {
            logger.info("Executing prefix command '{}{}' invoked by user '{}' ({})",
                    prefix, commandName, event.getAuthor().getAsTag(), event.getAuthor().getId());
            command.execute(event, args);
        } catch (Exception ex) {
            logger.error("Error executing prefix command '{}{}': {}", prefix, commandName, ex.getMessage(), ex);
            event.getMessage()
                    .reply("⚠️ An error occurred while processing the command.")
                    .queue();
        }
    }
}
