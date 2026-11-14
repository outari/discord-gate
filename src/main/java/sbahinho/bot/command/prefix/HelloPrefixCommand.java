package sbahinho.bot.command.prefix;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sbahinho.bot.command.PrefixCommand;

/**
 * Prefix command '!hello' that replies with a greeting to the invoking user.
 */
public class HelloPrefixCommand implements PrefixCommand {

    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public String getDescription() {
        return "Replies with a greeting to the invoking user";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String authorName = event.getAuthor().getEffectiveName();
        event.getMessage()
             .reply(String.format("Hello, %s! 👋 Hope you are having a wonderful day!", authorName))
             .queue();
    }
}
