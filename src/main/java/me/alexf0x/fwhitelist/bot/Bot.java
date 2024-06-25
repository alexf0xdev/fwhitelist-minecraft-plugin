package me.alexf0x.fwhitelist.bot;

import me.alexf0x.fwhitelist.FWhitelist;
import me.alexf0x.fwhitelist.bot.commands.AddCommand;
import me.alexf0x.fwhitelist.bot.commands.InfoCommand;
import me.alexf0x.fwhitelist.bot.commands.RemoveCommand;
import me.alexf0x.fwhitelist.bot.listeners.GuildMemberRemoveListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot {

    private final FWhitelist plugin;

    private JDA jda;
    private Guild guild;
    private TextChannel textChannel;

    public Bot(FWhitelist plugin) {
        this.plugin = plugin;
        start();
    }

    public void start() {
        try {
            String botToken = plugin.getConfig().getString("discord.bot-token");

            if (botToken.isEmpty()) {
                plugin.sendConsole("&cБот токен не указан в конфигурации. Discord бот не будет включен");
                return;
            }

            jda = JDABuilder.createDefault(botToken)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.playing(plugin.getConfig().getString("discord.activity")))
                    .build();

            jda.addEventListener(new InfoCommand(plugin));
            jda.addEventListener(new AddCommand(plugin));
            jda.addEventListener(new RemoveCommand(plugin));
            jda.addEventListener(new GuildMemberRemoveListener(plugin));

            jda.awaitReady();

            String guildId = plugin.getConfig().getString("discord.guild-id");
            String channelId = plugin.getConfig().getString("discord.channel-id");

            if (guildId.isEmpty()) {
                plugin.sendConsole("&cSpecify the Discord server id in the configuration. Discord bot is disabled...");
                stop();
                return;
            }

            guild = jda.getGuildById(guildId);

            if (guild == null) {
                plugin.sendConsole("&cIncorrect Discord server id. Discord bot disconnects....");
                stop();
                return;
            }

            if (channelId.isEmpty()) {
                plugin.sendConsole("&cSpecify the text channel id in the configuration. Discord bot disconnects....");
                stop();
                return;
            }

            textChannel = guild.getTextChannelById(channelId);

            if (textChannel == null) {
                plugin.sendConsole("&cInvalid text channel id. Discord bot disconnects....");
                stop();
                return;
            }

            guild.updateCommands().addCommands(
                    Commands.slash("info", plugin.getConfig().getString("messages.discord.info-description")),
                    Commands.slash("add", plugin.getConfig().getString("messages.discord.add-description"))
                            .addOption(OptionType.STRING, "minecraft_nickname", plugin.getConfig().getString("messages.discord.add-option-description"), true),
                    Commands.slash("remove", plugin.getConfig().getString("messages.discord.remove-description"))
            ).queue();

            plugin.sendConsole("&aDiscord bot successfully launched");
        } catch (InterruptedException e) {
            plugin.sendConsole("Error: " + e.getMessage());
        }
    }

    public void stop() {
        if (jda == null) return;

        jda.shutdown();
    }

    public void restart() {
        if (jda == null) return;

        stop();
        start();
    }
}
