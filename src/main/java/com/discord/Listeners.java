package com.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Listeners extends ListenerAdapter {
    public static File saves = new File("src\\main\\java\\com\\discord\\Info\\Numbers.txt");
    public static Map<String, Integer> serverNumbers = new ConcurrentHashMap<>();
    public static Map<String, String> lastCounter = new ConcurrentHashMap<>();


    public void loadNumbersFromFile() throws FileNotFoundException {
        Scanner fs = new Scanner(saves);
        while (fs.hasNextLine()) {
            String line = fs.nextLine().trim();
            if (!line.isEmpty()) {
                String[] parts = line.split(" ");
                serverNumbers.put(parts[0], Integer.valueOf(parts[1]));
            }
        }
    }

    public void saveNumbersToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saves))) {
            for (var entry : serverNumbers.entrySet())
                writer.println(entry.getKey() + " " + entry.getValue());
        }
    }


    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();

        for (Guild guild : jda.getGuilds()) {
            guild.updateCommands()
                    .addCommands(
                            Commands.slash("timeout", "Times a user out")
                                    .addOption(OptionType.USER, "user", "User to timeout", true)
                                    .addOption(OptionType.STRING, "time", "Timeout Length", false)
                                    .addOption(OptionType.STRING, "reason", "Reason for timeout", false),
                            Commands.slash("whisper", "Whispers to a user")
                                    .addOption(OptionType.USER, "user", "User to whisper to", true)
                                    .addOption(OptionType.STRING, "message", "The message to send", true),
                            Commands.slash("echo", "Echos your text")
                                    .addOption(OptionType.STRING, "message", "Message to echo", true),
                            Commands.slash("user_info", "Gets a User's info")
                                    .addOption(OptionType.USER, "user", "User to get info", false),
                            Commands.slash("set_player_tag", "Connects supercell account")
                                    .addOption(OptionType.STRING, "tag", "Player tag starting with #", true),
                            Commands.slash("get_player_tag", "Gets a user's player tag")
                                    .addOption(OptionType.USER, "user", "User to search database", false),
                            Commands.slash("trophies", "Gets Player Trophies")
                                    .addOption(OptionType.USER, "user", "User to get trophies", false),
                            Commands.slash("player_info", "Gets a Player's info")
                                    .addOption(OptionType.USER, "user", "User to get info", false),
                            Commands.slash("battlelog", "Gets a Player's battle log")
                                    .addOption(OptionType.USER, "user", "User's battle log", false)
                    )
                    .queue();
        }

        try {
            loadNumbersFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        String guildId = guild.getId();
        serverNumbers.putIfAbsent(guildId, 0);

        if (event.getAuthor().isBot()) return;

        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        String messageContent = message.getContentRaw();

        if (channel.getName().equalsIgnoreCase("123")) {
            Double number = null;
            for (String part : messageContent.split(" ")) {
                try {
                    number = Double.parseDouble(part);
                    break;
                } catch (NumberFormatException ignored) {
                }
            }
            if (number == null) return;

            int expected = serverNumbers.get(guildId) + 1;
            String authorId = message.getAuthor().getId();

            if (Math.round(number) == expected && !authorId.equals(lastCounter.get(guildId))) {
                message.addReaction(Emoji.fromUnicode("U+2714")).queue();
                lastCounter.put(guildId, authorId);
                serverNumbers.put(guildId, expected);
                try {
                    saveNumbersToFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                message.addReaction(Emoji.fromUnicode("U+274C")).queue();
            }
        }

        if (messageContent.equalsIgnoreCase("%ping")) {
            long ping = event.getJDA().getGatewayPing();
            channel.sendMessage(":ping_pong: Pong! " + ping + "ms").queue();
        }
    }
}
