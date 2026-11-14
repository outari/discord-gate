package sbahinho.bot.command;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sbahinho.bot.command.prefix.HelloPrefixCommand;
import sbahinho.bot.command.prefix.PingPrefixCommand;
import sbahinho.bot.command.slash.PingSlashCommand;

import static org.junit.jupiter.api.Assertions.*;

class CommandDataTest {

    @Test
    @DisplayName("PingSlashCommand should define valid command data")
    void testPingSlashCommandData() {
        PingSlashCommand pingSlash = new PingSlashCommand();
        SlashCommandData data = pingSlash.getCommandData();

        assertNotNull(data);
        assertEquals("ping", data.getName());
        assertFalse(data.getDescription().isBlank());
    }

    @Test
    @DisplayName("HelloPrefixCommand should have valid command name and description")
    void testHelloPrefixCommandData() {
        HelloPrefixCommand hello = new HelloPrefixCommand();
        assertEquals("hello", hello.getName());
        assertFalse(hello.getDescription().isBlank());
    }

    @Test
    @DisplayName("PingPrefixCommand should have valid command name and description")
    void testPingPrefixCommandData() {
        PingPrefixCommand ping = new PingPrefixCommand();
        assertEquals("ping", ping.getName());
        assertFalse(ping.getDescription().isBlank());
    }
}
