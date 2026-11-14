package sbahinho.bot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Interface representing a prefix-based text command (e.g., !hello).
 * Implementations define the trigger keyword, description, and execution logic.
 */
public interface PrefixCommand {

    /**
     * Gets the command name/keyword (case-insensitive trigger).
     *
     * @return the command trigger name (without the prefix symbol)
     */
    String getName();

    /**
     * Gets a brief description of what this command does.
     *
     * @return description of the command
     */
    String getDescription();

    /**
     * Handles the message event when a user invokes this prefix command.
     *
     * @param event the {@link MessageReceivedEvent}
     * @param args  command arguments passed after the command name
     */
    void execute(MessageReceivedEvent event, String[] args);
}
