package com.discord.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Generic {
    public static void handleTimeout(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        Member target = event.getOption("user").getAsMember();
        String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "No reason provided";

        if (!event.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.getHook().sendMessage("❌ I do not have permission to timeout this member.").queue();
            return;
        }

        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS) || !event.getMember().canInteract(target)) {
            event.getHook().sendMessage("❌ You do not have permission to timeout this member.").queue();
            return;
        }

        long duration;
        TimeUnit unit;

        if (event.getOption("time") == null) {
            duration = 30;
            unit = TimeUnit.MINUTES;
        } else {
            String timeInput = event.getOption("time").getAsString().toLowerCase();

            try {
                char suffix = timeInput.charAt(timeInput.length() - 1);
                duration = Long.parseLong(timeInput.substring(0, timeInput.length() - 1));

                unit = switch (suffix) {
                    case 's' -> TimeUnit.SECONDS;
                    case 'm' -> TimeUnit.MINUTES;
                    case 'h' -> TimeUnit.HOURS;
                    case 'd' -> TimeUnit.DAYS;
                    default -> throw new IllegalArgumentException();
                };
            } catch (Exception e) {
                event.getHook().sendMessage("❌ Invalid time format. Use `20s`, `10m`, `2h`, or `1d`.").queue();
                return;
            }
        }

        event.getGuild().timeoutFor(target, duration, unit).queue(
                success -> {
                    event.getHook().sendMessage("✅ Successfully timed out **" + target.getUser().getAsTag() + "**.").queue();
                    EmbedBuilder publicEmbed = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("⏱️ Member Timed Out")
                            .addField("User", target.getUser().getAsTag(), true)
                            .addField("Duration", event.getOption("time").getAsString(), true)
                            .addField("Reason", reason, false)
                            .setFooter("Requested by: " + event.getUser().getAsTag());
                    event.getChannel().sendMessageEmbeds(publicEmbed.build()).queue();
                },
                failure -> event.getHook().sendMessage("❌ Failed to timeout: " + failure.getMessage()).queue()
        );
    }

    public static void handleWhisper(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        Member recipient = event.getOption("user").getAsMember();
        String content = event.getOption("message").getAsString();

        recipient.getUser().openPrivateChannel()
                .flatMap(channel -> channel.sendMessage("**" + event.getUser().getAsTag() + "** whispers to you:\n> *\"" + content + "\"*"))
                .queue(
                        success -> event.getHook().sendMessage("📨 Whisper sent!").queue(),
                        failure -> event.getHook().sendMessage("❌ Could not send whisper.").queue()
                );
    }

    public static void handleEcho(SlashCommandInteractionEvent event) {
        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Echo")
                .setColor(Color.GREEN)
                .setDescription(event.getOption("message").getAsString())
                .setFooter("Sent by: " + event.getUser().getAsTag())
                .build()).queue();
    }

    public static void handleUserInfo(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Member member = event.getOption("user") == null ? event.getMember() : event.getOption("user").getAsMember();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("User Information")
                .setColor(Color.ORANGE)
                .setDescription("`/user info`: get information and statistics about a user.")
                .addField("Nickname", member.getEffectiveName(), true)
                .addField("User Tag", member.getUser().getAsTag(), true)
                .addField("Status", member.getOnlineStatus().toString(), true)
                .addField("Role", member.getRoles().isEmpty() ? "None" : member.getRoles().getFirst().getName(), true)
                .addField("Boost Status", String.valueOf(member.isBoosting()), true)
                .addField("\u200B", "\u200B", true)
                .addField("Joined Server", member.getTimeJoined().toLocalDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")), false)
                .setThumbnail(member.getUser().getAvatarUrl())
                .setFooter("Requested by: " + event.getUser().getAsTag());

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
