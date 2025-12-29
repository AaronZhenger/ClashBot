package com.discord.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class PlayerTags {
    public static HashMap<Long, String> readPlayerTags() throws FileNotFoundException {
        HashMap<Long, String> tags = new HashMap<>();
        File saves = new File("src\\main\\java\\com\\discord\\Info\\PlayerTags.txt");

        Scanner fs = new Scanner(saves);
        while (fs.hasNextLine()) {
            String line = fs.nextLine().trim();
            if (!line.isEmpty()) {
                String[] parts = line.split(" ");
                tags.put(Long.valueOf(parts[0]), parts[1]);
            }
        }
        return tags;
    }

    public static void writePlayerTags(HashMap<Long, String> tags) throws IOException {
        File saves = new File("src\\main\\java\\com\\discord\\Info\\PlayerTags.txt");
        if (!saves.exists()) saves.createNewFile();

        try (PrintWriter pw = new PrintWriter(new FileWriter(saves))) {
            for (var entry : tags.entrySet())
                pw.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public static void handleSetPlayerTag(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();

        String tag = event.getOption("tag").getAsString().toUpperCase();
        if (!tag.startsWith("#")) tag = "#" + tag;

        if (tag.length() != 10) {
            tagInvalid(event);
            return;
        }

        HashMap<Long, String> tags = readPlayerTags();
        tags.put(event.getUser().getIdLong(), tag);
        writePlayerTags(tags);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("✅ Successfully Stored Player Tag")
                .setColor(Color.GREEN)
                .setDescription("Welcome to our community!")
                .setFooter("Requested by: " + event.getUser().getAsTag())
                .build()).queue();
    }

    public static void handleGetPlayerTag(SlashCommandInteractionEvent event) throws FileNotFoundException {
        event.deferReply().queue();

        HashMap<Long, String> tags = readPlayerTags();
        long userId = event.getOption("user") == null ? event.getMember().getIdLong() :
                event.getOption("user").getAsUser().getIdLong();

        if (!tags.containsKey(userId)) {
            event.getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("❌ User has not connected account")
                    .setColor(Color.RED)
                    .setDescription("Try `/set_player_tag` to get started.")
                    .setFooter("Requested by: " + event.getUser().getAsTag())
                    .build()).queue();
            return;
        }

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(tags.get(userId))
                .setColor(Color.GREEN)
                .setDescription(event.getOption("user").getAsUser().getAsTag() + "'s player tag")
                .setFooter("Requested by: " + event.getUser().getAsTag())
                .build()).queue();
    }

    public static void tagInvalid(SlashCommandInteractionEvent event) {
        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("❌ Player Tag Invalid")
                .setColor(Color.RED)
                .setDescription("Player tags can be found under Clash Royale profiles")
                .setFooter("Requested by: " + event.getUser().getAsTag())
                .build()).queue();
    }
}
