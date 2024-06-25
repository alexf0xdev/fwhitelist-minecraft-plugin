package me.alexf0x.fwhitelist.bot.commands;

import me.alexf0x.fwhitelist.FWhitelist;
import me.alexf0x.fwhitelist.models.WhitelistedPlayer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InfoCommand extends ListenerAdapter {

    private final FWhitelist plugin;

    public InfoCommand(FWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("info")) return;

        String channelId = plugin.getConfig().getString("discord.channel-id");

        if (!event.getChannelId().equals(channelId)) {
            event.reply(plugin.getConfig().getString("messages.discord.only-in-channel")
                    .replace("{channel}", "<#" + channelId + ">")).setEphemeral(true).queue();
            return;
        }

        Member member = event.getMember();
        String discordId = member.getId();

        WhitelistedPlayer whitelistedPlayer = plugin.getStorage().getWhitelistedPlayerByDiscordId(discordId);

        if (whitelistedPlayer == null) {
            event.reply(plugin.getConfig().getString("messages.discord.not-connected")).queue();
            return;
        }

        event.reply(plugin.getConfig().getString("messages.discord.info")
                .replace("{player}", whitelistedPlayer.getPlayerName())).queue();
    }
}
