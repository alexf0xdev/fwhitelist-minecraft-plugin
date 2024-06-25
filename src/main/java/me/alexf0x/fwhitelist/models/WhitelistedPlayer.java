package me.alexf0x.fwhitelist.models;

public class WhitelistedPlayer {

    private String playerName;
    private String discordId;

    public WhitelistedPlayer(String playerName, String discordId) {
        this.playerName = playerName;
        this.discordId = discordId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getDiscordId() {
        return discordId;
    }
}
