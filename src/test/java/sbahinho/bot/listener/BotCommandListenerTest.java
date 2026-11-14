package sbahinho.bot.listener;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sbahinho.bot.command.prefix.HelloPrefixCommand;
import sbahinho.bot.command.prefix.PingPrefixCommand;
import sbahinho.bot.command.slash.PingSlashCommand;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BotCommandListenerTest {

    private BotCommandListener listener;

    @BeforeEach
    void setUp() {
        listener = new BotCommandListener("!");
    }

    @Test
    @DisplayName("Should correctly register slash commands")
    void testRegisterSlashCommand() {
        PingSlashCommand pingSlash = new PingSlashCommand();
        listener.registerSlashCommand(pingSlash);

        assertTrue(listener.getSlashCommands().containsKey("ping"));
        assertSame(pingSlash, listener.getSlashCommands().get("ping"));

        List<SlashCommandData> dataList = listener.getSlashCommandDataList();
        assertEquals(1, dataList.size());
        assertEquals("ping", dataList.getFirst().getName());
    }

    @Test
    @DisplayName("Should correctly register prefix commands")
    void testRegisterPrefixCommand() {
        HelloPrefixCommand hello = new HelloPrefixCommand();
        PingPrefixCommand ping = new PingPrefixCommand();

        listener.registerPrefixCommand(hello)
                .registerPrefixCommand(ping);

        assertTrue(listener.getPrefixCommands().containsKey("hello"));
        assertTrue(listener.getPrefixCommands().containsKey("ping"));
        assertSame(hello, listener.getPrefixCommands().get("hello"));
        assertSame(ping, listener.getPrefixCommands().get("ping"));
    }

    @Test
    @DisplayName("Should use default prefix if null or blank is provided")
    void testDefaultPrefixFallback() {
        BotCommandListener listenerWithNull = new BotCommandListener(null);
        assertEquals("!", listenerWithNull.getPrefix());

        BotCommandListener listenerWithBlank = new BotCommandListener("   ");
        assertEquals("!", listenerWithBlank.getPrefix());
    }
}
