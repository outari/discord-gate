package sbahinho.bot.command.prefix;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sbahinho.bot.command.PrefixCommand;

/**
 * Prefix command '!ping' that replies with gateway latency.
 */
public class PingPrefixCommand implements PrefixCommand {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Replies with Pong! and Discord gateway latency";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        long gatewayLatency = event.getJDA().getGatewayPing();
        event.getMessage()
             .reply(String.format("Pong! 🏓 Gateway latency: %d ms", gatewayLatency))
             .queue();
    }
}
