package me.alexf0x.fwhitelist.bot.commands;

import me.alexf0x.fwhitelist.FWhitelist;
import me.alexf0x.fwhitelist.models.WhitelistedPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AddCommand extends ListenerAdapter {

    private final FWhitelist plugin;

    public AddCommand(FWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("add")) return;

        String channelId = plugin.getConfig().getString("discord.channel-id");

        if (!event.getChannelId().equals(channelId)) {
            event.reply(plugin.getConfig().getString("messages.discord.only-in-channel")
                    .replace("{channel}", "<#" + channelId + ">")).setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        Member member = event.getMember();
        String discordId = member.getId();

        String playerName = event.getOption("minecraft_nickname").getAsString();

        WhitelistedPlayer whitelistedPlayerByName = plugin.getStorage().getWhitelistedPlayerByName(playerName);
        WhitelistedPlayer whitelistedPlayerByDiscordId = plugin.getStorage().getWhitelistedPlayerByDiscordId(discordId);

        if (whitelistedPlayerByName != null) {
            event.reply(plugin.getConfig().getString("messages.discord.already-in-whitelist")
                    .replace("{player}", whitelistedPlayerByName.getPlayerName())).queue();
            return;
        }

        if (whitelistedPlayerByDiscordId != null) {
            event.reply(plugin.getConfig().getString("messages.discord.already-connected")
                    .replace("{player}", whitelistedPlayerByDiscordId.getPlayerName())).queue();
            return;
        }

        plugin.getStorage().addWhitelistedPlayer(playerName, discordId);

        String whitelistedRoleId = plugin.getConfig().getString("discord.whitelisted-role-id");

        if (!whitelistedRoleId.isEmpty()) {
            Role whitelistedRole = guild.getRoleById(whitelistedRoleId);

            if (whitelistedRole != null) {
                guild.addRoleToMember(member, whitelistedRole).queue();
            }
        }

        if (guild.getSelfMember().canInteract(member)) {
            member.modifyNickname(playerName).queue();
        }

        event.reply(plugin.getConfig().getString("messages.discord.added-to-whitelist")
                .replace("{player}", playerName)).queue();
    }
}
