package me.alexf0x.fwhitelist.commands;

import me.alexf0x.fwhitelist.FWhitelist;
import me.alexf0x.fwhitelist.models.WhitelistedPlayer;
import me.alexf0x.fwhitelist.storages.JsonStorage;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.alexf0x.fwhitelist.FWhitelist.colored;

public class WhitelistCommand implements CommandExecutor, TabCompleter {

    private final FWhitelist plugin;

    public WhitelistCommand(FWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(colored(plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (strings.length == 0) {
            getHelpMessage(commandSender);
            return true;
        }

        boolean whitelist = plugin.getConfig().getBoolean("whitelist");

        switch (strings[0]) {
            case "on":
                if (whitelist) {
                    commandSender.sendMessage(colored(plugin.getConfig().getString("messages.already-enabled")));
                    break;
                }

                plugin.getConfig().set("whitelist", true);
                plugin.saveConfig();

                commandSender.sendMessage(colored(plugin.getConfig().getString("messages.enabled")));
                break;
            case "off":
                if (!whitelist) {
                    commandSender.sendMessage(colored(plugin.getConfig().getString("messages.already-disabled")));
                    break;
                }

                plugin.getConfig().set("whitelist", false);
                plugin.saveConfig();

                commandSender.sendMessage(colored(plugin.getConfig().getString("messages.disabled")));
                break;
            case "add":
                if (strings.length < 3) {
                    commandSender.sendMessage(colored(plugin.getConfig().getString("messages.not-enough-arguments")));
                    break;
                }

                String playerName = strings[1];

                WhitelistedPlayer whitelistedPlayer = plugin.getStorage().getWhitelistedPlayerByName(playerName);

                if (whitelistedPlayer != null) {
                    commandSender.sendMessage(colored(plugin.getConfig().getString("messages.already-in-whitelist")
                            .replace("{player}", playerName)));
                    break;
                }

                plugin.getStorage().addWhitelistedPlayer(playerName, strings[2]);

                commandSender.sendMessage(colored(plugin.getConfig().getString("messages.added-to-whitelist")
                        .replace("{player}", playerName)));
                break;
            case "remove":
                if (strings.length < 2) {
                    commandSender.sendMessage(colored(plugin.getConfig().getString("messages.not-enough-arguments")));
                    break;
                }

                playerName = strings[1];

                whitelistedPlayer = plugin.getStorage().getWhitelistedPlayer(playerName);

                if (whitelistedPlayer == null) {
                    commandSender.sendMessage(colored(plugin.getConfig().getString("messages.not-in-whitelist")
                            .replace("{player}", playerName)));
                    break;
                }

                playerName = whitelistedPlayer.getPlayerName();

                plugin.getStorage().removeWhitelistedPlayer(playerName);

                commandSender.sendMessage(colored(plugin.getConfig().getString("messages.removed-from-whitelist")
                        .replace("{player}", playerName)));
                break;
            case "list":
                List<WhitelistedPlayer> whitelistedPlayers = plugin.getStorage().getWhitelistedPlayers();

                if (whitelistedPlayers.isEmpty()) {
                    commandSender.sendMessage(colored(plugin.getConfig().getString("messages.list-empty")));
                    break;
                }

                commandSender.sendMessage(colored(plugin.getConfig().getString("messages.list")));

                for (WhitelistedPlayer whitelistedPlayer1 : whitelistedPlayers) {
                    commandSender.sendMessage(colored(plugin.getConfig().getString("messages.list-item")
                            .replace("{player}", whitelistedPlayer1.getPlayerName())
                            .replace("{discordId}", whitelistedPlayer1.getDiscordId())));
                }
                break;
            case "reload":
                plugin.reload();
                commandSender.sendMessage(colored(plugin.getConfig().getString("messages.reloaded")));
                break;
            case "help":
                getHelpMessage(commandSender);
                break;
            default:
                getHelpMessage(commandSender);
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> complete = new ArrayList<>();

        if (!commandSender.isOp()) return complete;

        if (strings.length == 1) complete.addAll(Arrays.asList("on", "off", "add", "remove", "list", "reload", "help"));

        if (strings.length == 2 && strings[0].equals("add")) {
            OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

            for (OfflinePlayer offlinePlayer : players) {
                complete.add(offlinePlayer.getName());
            }
        }

        if (strings.length == 2 && strings[0].equals("remove")) {
            List<WhitelistedPlayer> whitelistedPlayers = plugin.getStorage().getWhitelistedPlayers();

            for (WhitelistedPlayer whitelistedPlayer : whitelistedPlayers) {
                complete.add(whitelistedPlayer.getPlayerName());
            }
        }

        return complete;
    }

    public void getHelpMessage(CommandSender commandSender) {
        for (String string : plugin.getConfig().getStringList("messages.help")) {
            commandSender.sendMessage(colored(string));
        }
    }
}
