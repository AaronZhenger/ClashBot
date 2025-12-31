package com.discord.Commands.PlayerInfo;

import com.discord.Commands.PlayerTags;
import com.discord.Commands.PlayerWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerInfoSlash {
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
                .setColor(new Color(234, 198, 38))
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
                            + player.getString("name") + " (" + user.getUser().getAsTag() + ") Info")
                    .setThumbnail(yp.getJSONObject("iconUrls").getString("large"))
                    .setColor(new Color(33, 133, 208))
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
                    .build())
//                .addActionRow(Button.danger("battleLog", "My Battles"))
                .queue();
    }

    public static void handleBattleLog(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();

        HashMap<Long, String> tags = PlayerTags.readPlayerTags();
        Member user = event.getOption("user") == null ? event.getMember() :
                event.getOption("user").getAsMember();

        JSONObject player = PlayerWrapper.getPlayerStuff(tags.get(user.getIdLong()));
        JSONArray battles = PlayerWrapper.getPlayerBattles(tags.get(user.getIdLong()));
        if (player == null || battles == null) {
            PlayerTags.tagInvalid(event);
            return;
        }

        JSONArray array = new JSONArray(player.getJSONArray("badges"));

        JSONObject yp = array.getJSONObject(0);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("name").equals("YearsPlayed")) {
                yp = obj;
                break;
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("<:battle:1455747385870258259>" + player.getString("name")
                        + " (" + user.getUser().getAsTag() + ") Battle Log")
                .setThumbnail(yp.getJSONObject("iconUrls").getString("large"))
                .setColor(new Color(219, 40, 40))
                .setDescription(tags.get(user.getIdLong()))
                .setFooter("Requested by: " + event.getUser().getAsTag());

        for (int i = 0; i < Math.min(6, battles.length()); i++) {
            JSONObject battle = battles.getJSONObject(i);
            String result = "Draw";
            int dif = battle.getJSONArray("team").getJSONObject(0).getInt("crowns")-battle.getJSONArray("opponent").getJSONObject(0).getInt("crowns");
            if (dif>0) result = "Victory";
            else if (dif<0) result = "Defeat";
            String gameMode = battle.getJSONObject("gameMode").getString("name").equals("7xElixir_Friendly_EventDeck")
                    ? "7x Elixir" : battle.getJSONObject("gameMode").getString("name");
            embed.addField(
                    "\u200B",
                    "**"+gameMode+" "+result+" ("
                            +battle.getJSONObject("arena").getString("name")+")**",
                    false
            );
            boolean ladder = battle.getJSONArray("team").getJSONObject(0).has("startingTrophies");
            String add = result.equals("Defeat") ? " " : " +";
            String trophyHandling = "";
            trophyHandling+=(ladder ? battle.getJSONArray("team").getJSONObject(0).getInt("startingTrophies")+ add
                    + battle.getJSONArray("team").getJSONObject(0).getInt("trophyChange") : 0);
            String crowns = "";
            for (int j = 0; j < battle.getJSONArray("team").getJSONObject(0).getInt("crowns"); j++)
                crowns+="<:BlueCrown1:1455755331224207471> ";
            embed.addField(
                    battle.getJSONArray("team").getJSONObject(0).getString("name") + " ("
                            + trophyHandling + ")",
                    crowns+"**"+battle.getJSONArray("team").getJSONObject(0).getInt("crowns")+"**",
                    true
            );
            String addO = result.equals("Defeat") ? " +" : " ";
            String trophyHandlingO = "";
            trophyHandlingO+=(ladder ? battle.getJSONArray("opponent").getJSONObject(0).getInt("startingTrophies")+ addO
                    + battle.getJSONArray("opponent").getJSONObject(0).getInt("trophyChange") : 0);
            String crownsO = "";
            for (int j = 0; j < battle.getJSONArray("opponent").getJSONObject(0).getInt("crowns"); j++)
                crownsO+="<:RedCrown1:1455755351663050814> ";
            embed.addField(
                    battle.getJSONArray("opponent").getJSONObject(0).getString("name") + " ("
                            + trophyHandlingO + ")",
                    crownsO+"**"+battle.getJSONArray("opponent").getJSONObject(0).getInt("crowns")+"**",
                    true
            );
        }

        event.getHook().sendMessageEmbeds(embed.build())
//                .addActionRow(Button.primary("playerInfo", "My Stats"))
                .queue();
    }
}
