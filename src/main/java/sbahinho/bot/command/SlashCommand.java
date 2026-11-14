package sbahinho.bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Interface representing a Discord slash command.
 * Implementations define the command specification and the handler logic.
 */
public interface SlashCommand {

    /**
     * Gets the command registration data used to register this command with Discord.
     *
     * @return {@link SlashCommandData} for Discord API registration
     */
    SlashCommandData getCommandData();

    /**
     * Handles the interaction when the user invokes this slash command.
     *
     * @param event the interaction event
     */
    void execute(SlashCommandInteractionEvent event);
}
