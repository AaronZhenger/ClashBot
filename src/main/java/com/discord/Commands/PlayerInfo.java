package com.discord.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerInfo {
    public static void handleTrophies(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();

        HashMap<Long, String> tags = PlayerTags.readPlayerTags();
        Member user = event.getOption("user") == null ? event.getMember() :
                event.getOption("user").getAsMember();

        JSONObject player = PlayerWrapper.getPlayerStuff(tags.get(user.getIdLong()));
        if (player == null) {
            PlayerTags.tagInvalid(event);
            return;
        }
        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("<:crtrophy:1454328419893383225> " + player.getInt("trophies"))
                .setColor(Color.GREEN)
                .setDescription(user.getUser().getAsTag()
                        + " is in " + player.getJSONObject("arena").getString("name"))
                .setFooter("Requested by: " + event.getUser().getAsTag())
                .build()).queue();
    }

    public static void handleStats(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();

        HashMap<Long, String> tags = PlayerTags.readPlayerTags();
        Member user = event.getOption("user") == null ? event.getMember() :
                event.getOption("user").getAsMember();

        JSONObject player = PlayerWrapper.getPlayerStuff(tags.get(user.getIdLong()));
        if (player == null) {
            PlayerTags.tagInvalid(event);
            return;
        }

        Map<Integer, String> emojiIds = new HashMap<>();
        emojiIds.put(1, "1455035980397674496");
        emojiIds.put(2, "1455036022420275407");
        emojiIds.put(3, "1455036049033269459");
        emojiIds.put(4, "1455036083346870344");
        emojiIds.put(5, "1455036112283369600");
        emojiIds.put(6, "1455036139055481029");
        emojiIds.put(7, "1455036180470173859");

        boolean clan = player.has("clan");

        JSONArray array = new JSONArray(player.getJSONArray("badges"));

        JSONObject yp = array.getJSONObject(0);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("name").equals("YearsPlayed")) {
                yp = obj;
                break;
            }
        }

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("<:l" + player.getJSONObject("currentPathOfLegendSeasonResult").getInt("leagueNumber")
                        + ":" + emojiIds.get(player.getJSONObject("currentPathOfLegendSeasonResult").getInt("leagueNumber")) + "> "
                        + player.getString("name") + " (" + user.getUser().getAsTag() + ")")
                .setThumbnail(yp.getJSONObject("iconUrls").getString("large"))
                .setColor(Color.GREEN)
                .setDescription(tags.get(user.getIdLong()))
                .addField("Clan", clan ? player.getJSONObject("clan").getString("name") + " ("
                        + player.getJSONObject("clan").getString("tag") + ")"
                        : "N/A", true)
                .addField("Role", clan ? player.getString("role") : "N/A", true)
                .addField("\u200B", "\u200B", true)
                .addField("Level", String.valueOf(player.getInt("expLevel")), true)
                .addField("Trophies", String.valueOf(player.getInt("trophies")), true)
                .addField("Arena", player.getJSONObject("arena").getString("name"), true)
                .addField("Favorite Card", player.getJSONObject("currentFavouriteCard").getString("name"), true)
                .addField(
                        "Win %",
                        String.format(
                                "%.3f",
                                player.getInt("wins") / Double.parseDouble(String.valueOf(player.getInt("battleCount"))) * 100
                        )
                                + "%",
                        true)
                .addField(
                        "3 Crown %",
                        String.format(
                                "%.3f",
                                player.getInt("threeCrownWins") / Double.parseDouble(String.valueOf(player.getInt("wins"))) * 100
                        )
                                + "%",
                        true)
                .addField("--------------------", "\u200B", true)
                .addField("--------------------", "\u200B", true)
                .addField("--------------------", "\u200B", true)
                .setFooter("Requested by: " + event.getUser().getAsTag())
                .build()).queue();
    }
}
