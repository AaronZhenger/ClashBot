package com.discord;

import com.discord.Commands.PlayerInfo.PlayerInfoButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

public class Buttons extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        try {
            switch (event.getComponentId()) {
                case "battleLog" -> PlayerInfoButton.handleBattleLog(event);
                case "playerInfo" -> PlayerInfoButton.handleStats(event);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
