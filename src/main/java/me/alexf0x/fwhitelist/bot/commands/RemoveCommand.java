package me.alexf0x.fwhitelist.bot.commands;

import me.alexf0x.fwhitelist.FWhitelist;
import me.alexf0x.fwhitelist.models.WhitelistedPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RemoveCommand extends ListenerAdapter {

    private final FWhitelist plugin;

    public RemoveCommand(FWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("remove")) return;

        String channelId = plugin.getConfig().getString("discord.channel-id");

        if (!event.getChannelId().equals(channelId)) {
            event.reply(plugin.getConfig().getString("messages.discord.only-in-channel")
                    .replace("{channel}", "<#" + channelId + ">")).setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();

        Member member = event.getMember();
        String discordId = member.getId();

        WhitelistedPlayer whitelistedPlayer = plugin.getStorage().getWhitelistedPlayerByDiscordId(discordId);

        if (whitelistedPlayer == null) {
            event.reply(plugin.getConfig().getString("messages.discord.not-connected")).queue();
            return;
        }

        String playerName = whitelistedPlayer.getPlayerName();

        plugin.getStorage().removeWhitelistedPlayer(playerName);

        String whitelistedRoleId = plugin.getConfig().getString("discord.whitelisted-role-id");

        if (!whitelistedRoleId.isEmpty()) {
            Role whitelistedRole = guild.getRoleById(whitelistedRoleId);

            if (whitelistedRole != null) {
                guild.removeRoleFromMember(member, whitelistedRole).queue();
            }
        }

        if (guild.getSelfMember().canInteract(member)) {
            member.modifyNickname("").queue();
        }

        event.reply(plugin.getConfig().getString("messages.discord.removed-from-whitelist")
                .replace("{player}", playerName)).queue();
    }
}
