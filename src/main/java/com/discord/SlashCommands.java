package com.discord;

import com.discord.Commands.Generic;
import com.discord.Commands.PlayerInfo.PlayerInfoSlash;
import com.discord.Commands.PlayerTags;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.*;

public class SlashCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {
            switch (event.getName().toLowerCase()) {
                case "timeout" -> Generic.handleTimeout(event);
                case "whisper" -> Generic.handleWhisper(event);
                case "echo" -> Generic.handleEcho(event);
                case "user_info" -> Generic.handleUserInfo(event);
                case "set_player_tag" -> PlayerTags.handleSetPlayerTag(event);
                case "get_player_tag" -> PlayerTags.handleGetPlayerTag(event);
                case "trophies" -> PlayerInfoSlash.handleTrophies(event);
                case "player_info" -> PlayerInfoSlash.handleStats(event);
                case "battlelog" -> PlayerInfoSlash.handleBattleLog(event);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
