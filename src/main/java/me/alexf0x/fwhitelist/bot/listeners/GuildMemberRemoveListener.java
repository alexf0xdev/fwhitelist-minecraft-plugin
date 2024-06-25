package me.alexf0x.fwhitelist.bot.listeners;

import me.alexf0x.fwhitelist.FWhitelist;
import me.alexf0x.fwhitelist.models.WhitelistedPlayer;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberRemoveListener extends ListenerAdapter {

    public final FWhitelist plugin;

    public GuildMemberRemoveListener(FWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User user = event.getUser();
        String discordId = user.getId();

        WhitelistedPlayer whitelistedPlayer = plugin.getStorage().getWhitelistedPlayerByDiscordId(discordId);

        if (whitelistedPlayer == null) return;

        plugin.getStorage().removeWhitelistedPlayer(whitelistedPlayer.getPlayerName());
    }
}
