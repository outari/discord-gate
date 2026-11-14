package sbahinho.bot.command.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import sbahinho.bot.command.SlashCommand;

/**
 * Slash command '/ping' that replies with 'Pong!' and Discord gateway latency.
 */
public class PingSlashCommand implements SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("ping", "Replies with Pong! and the Discord gateway latency");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long gatewayLatency = event.getJDA().getGatewayPing();
        event.reply(String.format("Pong! 🏓 Gateway latency: %d ms", gatewayLatency)).queue();
    }
}
