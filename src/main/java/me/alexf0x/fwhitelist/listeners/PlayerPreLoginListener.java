package me.alexf0x.fwhitelist.listeners;

import me.alexf0x.fwhitelist.FWhitelist;
import me.alexf0x.fwhitelist.models.WhitelistedPlayer;
import me.alexf0x.fwhitelist.storages.JsonStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import static me.alexf0x.fwhitelist.FWhitelist.colored;

public class PlayerPreLoginListener implements Listener {

    private final FWhitelist plugin;

    public PlayerPreLoginListener(FWhitelist plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getConfig().getBoolean("whitelist")) return;

        String playerName = event.getName();

        WhitelistedPlayer whitelistedPlayer = plugin.getStorage().getWhitelistedPlayer(playerName);

        if (whitelistedPlayer != null) return;

        plugin.sendConsole("&fPlayer " + playerName + " tried to join, but he is not in whitelist");

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Component.text(colored(plugin.getConfig().getString("messages.whitelist")
                .replace("{player}", playerName))));
    }
}
