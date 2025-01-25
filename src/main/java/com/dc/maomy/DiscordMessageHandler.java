package com.dc.maomy;

import cn.nukkit.plugin.PluginBase;
import org.javacord.api.DiscordApi;

public class DiscordMessageHandler {

    private final PluginBase plugin;
    private final DiscordApi discordApi;
    private final ConfigManager configManager;

    private final DiscordToMinecraftHandler discordToMinecraftHandler;
    private final DiscordConsoleHandler discordConsoleHandler;
    private final DiscordActivityUpdater discordActivityUpdater;

    public DiscordMessageHandler(PluginBase plugin, DiscordApi discordApi, ConfigManager configManager) {
        this.plugin = plugin;
        this.discordApi = discordApi;
        this.configManager = configManager;

        this.discordToMinecraftHandler = new DiscordToMinecraftHandler(plugin, discordApi, configManager);
        this.discordConsoleHandler = new DiscordConsoleHandler(plugin, discordApi, configManager);
        this.discordActivityUpdater = new DiscordActivityUpdater(discordApi, configManager);
    }

    public void setupListeners() {
        discordActivityUpdater.updateActivity();

        if (configManager.isDiscordToMinecraftEnabled()) {
            discordToMinecraftHandler.setupListener();
        }

        if (configManager.isDiscordConsoleEnabled()) {
            discordConsoleHandler.setupListener();
        }
    }
}