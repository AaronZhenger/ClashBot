package com.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault(Tokens.token).enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS
        ).build();
        jda.addEventListener(new Listeners());
        jda.addEventListener(new SlashCommands());
    }
}