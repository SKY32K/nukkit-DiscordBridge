package com.dc.maomy;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;

public class ConfigManager {

    private final PluginBase plugin;
    private Config config;
    private String botToken;
    private String channelId;
    private String botStatusType;
    private String botStatusMessage;
    private boolean enableDiscordToMinecraft;
    private boolean enableMinecraftToDiscord;
    private String discordToMinecraftMessageFormat;
    private String minecraftToDiscordMessageFormat;
    private String playerJoinMessageFormat;
    private String playerQuitMessageFormat;
    private String serverStartMessage;
    private String serverStopMessage;

    public ConfigManager(PluginBase plugin) {
        this.plugin = plugin;
    }

    public boolean loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = new Config(configFile, Config.YAML);
        botToken = config.getString("botToken");
        channelId = config.getString("channelId");
        botStatusType = config.getString("botStatus.type", "PLAYING");
        botStatusMessage = config.getString("botStatus.message", "Minecraft with Nukkit");
        enableDiscordToMinecraft = config.getBoolean("enableDiscordToMinecraft", true);
        enableMinecraftToDiscord = config.getBoolean("enableMinecraftToDiscord", true);
        discordToMinecraftMessageFormat = config.getString("messages.discordToMinecraft", "[Discord] {username}: {message}");
        minecraftToDiscordMessageFormat = config.getString("messages.minecraftToDiscord", "[Minecraft] {username}: {message}");
        playerJoinMessageFormat = config.getString("messages.playerJoin", "[Minecraft] {username} 加入了遊戲！");
        playerQuitMessageFormat = config.getString("messages.playerQuit", "[Minecraft] {username} 離開了遊戲！");
        serverStartMessage = config.getString("messages.serverStart", "伺服器已啟動！");
        serverStopMessage = config.getString("messages.serverStop", "伺服器已關閉！");

        if (botToken == null || botToken.isEmpty() || channelId == null || channelId.isEmpty()) {
            plugin.getLogger().error("請在 config.yml 中設置 botToken 和 channelId！");
            return false;
        }

        return true;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getBotStatusType() {
        return botStatusType;
    }

    public String getBotStatusMessage() {
        return botStatusMessage;
    }

    public boolean isDiscordToMinecraftEnabled() {
        return enableDiscordToMinecraft;
    }

    public boolean isMinecraftToDiscordEnabled() {
        return enableMinecraftToDiscord;
    }

    public String getDiscordToMinecraftMessageFormat() {
        return discordToMinecraftMessageFormat;
    }

    public String getMinecraftToDiscordMessageFormat() {
        return minecraftToDiscordMessageFormat;
    }

    public String getPlayerJoinMessageFormat() {
        return playerJoinMessageFormat;
    }

    public String getPlayerQuitMessageFormat() {
        return playerQuitMessageFormat;
    }

    public String getServerStartMessage() {
        return serverStartMessage;
    }

    public String getServerStopMessage() {
        return serverStopMessage;
    }

    public boolean isJoinMessagesEnabled() {
        return config.getBoolean("joinMessages", true);
    }

    public boolean isQuitMessagesEnabled() {
        return config.getBoolean("quitMessages", true);
    }

    public boolean isDeathMessagesEnabled() {
        return config.getBoolean("deathMessages", true);
    }

    public boolean isStartMessagesEnabled() {
        return config.getBoolean("startMessages", true);
    }

    public boolean isStopMessagesEnabled() {
        return config.getBoolean("stopMessages", true);
    }

    public String getPlayerDeathMessageFormat() {
        return config.getString("messages.playerDeath", "[Minecraft] {username} 死亡了！");
    }

    public boolean isDiscordConsoleEnabled() {
        return config.getBoolean("discordConsole", false);
    }

    public String getConsoleChannelId() {
        return config.getString("consoleChannelId", "");
    }

    public String getConsoleRole() {
        return config.getString("consoleRole", "");
    }

    public boolean isConsoleStatusMessagesEnabled() {
        return config.getBoolean("consoleStatusMessages", true);
    }

    public boolean isLogConsoleCommandsEnabled() {
        return config.getBoolean("logConsoleCommands", true);
    }
}