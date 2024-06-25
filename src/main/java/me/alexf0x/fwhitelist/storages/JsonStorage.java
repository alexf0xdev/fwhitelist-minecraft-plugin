package me.alexf0x.fwhitelist.storages;

import com.google.gson.Gson;
import me.alexf0x.fwhitelist.FWhitelist;
import me.alexf0x.fwhitelist.models.WhitelistedPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonStorage {

    private final FWhitelist plugin;

    private ArrayList<WhitelistedPlayer> whitelistedPlayers = new ArrayList<>();

    public JsonStorage(FWhitelist plugin) {
        this.plugin = plugin;
        loadWhitelistedPlayers();
    }

    public List<WhitelistedPlayer> getWhitelistedPlayers() {
        return whitelistedPlayers;
    }

    public WhitelistedPlayer getWhitelistedPlayer(String string) {
        for (WhitelistedPlayer whitelistedPlayer : whitelistedPlayers) {
            if (whitelistedPlayer.getPlayerName().equals(string) || whitelistedPlayer.getDiscordId().equalsIgnoreCase(string)) {
                return whitelistedPlayer;
            }
        }
        return null;
    }

    public WhitelistedPlayer getWhitelistedPlayerByName(String playerName) {
        for (WhitelistedPlayer whitelistedPlayer : whitelistedPlayers) {
            if (whitelistedPlayer.getPlayerName().equalsIgnoreCase(playerName)) {
                return whitelistedPlayer;
            }
        }
        return null;
    }

    public WhitelistedPlayer getWhitelistedPlayerByDiscordId(String discordId) {
        for (WhitelistedPlayer whitelistedPlayer : whitelistedPlayers) {
            if (whitelistedPlayer.getDiscordId().equalsIgnoreCase(discordId)) {
                return whitelistedPlayer;
            }
        }
        return null;
    }

    public void addWhitelistedPlayer(String playerName, String discordId) {
        WhitelistedPlayer whitelistedPlayer = new WhitelistedPlayer(playerName, discordId);
        whitelistedPlayers.add(whitelistedPlayer);

        saveWhitelistedPlayers();
    }

    public void removeWhitelistedPlayer(String playerName) {
        for (WhitelistedPlayer whitelistedPlayer : whitelistedPlayers) {
            if (whitelistedPlayer.getPlayerName().equalsIgnoreCase(playerName)) {
                whitelistedPlayers.remove(whitelistedPlayer);
                break;
            }
        }
        saveWhitelistedPlayers();
    }

    public void loadWhitelistedPlayers() {
        try {
            File file = new File(plugin.getDataFolder(), "whitelist.json");

            if (!file.exists()) return;

            Gson gson = new Gson();
            Reader reader = new FileReader(file);

            WhitelistedPlayer[] whitelistedPlayersJson = gson.fromJson(reader, WhitelistedPlayer[].class);
            whitelistedPlayers = new ArrayList<>(Arrays.asList(whitelistedPlayersJson));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveWhitelistedPlayers() {
        try {
            File file = new File(plugin.getDataFolder(), "whitelist.json");
            Gson gson = new Gson();

            file.createNewFile();

            Writer writer = new FileWriter(file, false);

            gson.toJson(whitelistedPlayers, writer);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
