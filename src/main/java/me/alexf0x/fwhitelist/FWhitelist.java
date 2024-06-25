package me.alexf0x.fwhitelist;

import me.alexf0x.fwhitelist.commands.WhitelistCommand;
import me.alexf0x.fwhitelist.bot.Bot;
import me.alexf0x.fwhitelist.listeners.PlayerPreLoginListener;
import me.alexf0x.fwhitelist.storages.JsonStorage;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FWhitelist extends JavaPlugin {

    private PluginManager manager;

    private Bot bot;

    private JsonStorage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        manager = getServer().getPluginManager();

        manager.registerEvents(new PlayerPreLoginListener(this), this);

        getCommand("fwhitelist").setExecutor(new WhitelistCommand(this));
        getCommand("fwhitelist").setTabCompleter(new WhitelistCommand(this));

        storage = new JsonStorage(this);

        bot = new Bot(this);

        sendConsole("&aThe plugin has been successfully enabled");
        sendConsole("&7Developed by github.com/alexf0xdev");
    }

    @Override
    public void onDisable() {
        bot.stop();
    }

    public JsonStorage getStorage() {
        return storage;
    }

    public Bot getBot() {
        return bot;
    }

    public void reload() {
        reloadConfig();
        bot.restart();
    }

    public void sendConsole(String message) {
        getServer().getConsoleSender().sendMessage(colored("[" + getName() + "] " + message));
    }

    public static String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
