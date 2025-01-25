package com.dc.maomy;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;

public class DiscordActivityUpdater {

    private final DiscordApi discordApi;
    private final ConfigManager configManager;

    public DiscordActivityUpdater(DiscordApi discordApi, ConfigManager configManager) {
        this.discordApi = discordApi;
        this.configManager = configManager;
    }

    public void updateActivity() {
        discordApi.updateActivity(ActivityType.valueOf(configManager.getBotStatusType()), configManager.getBotStatusMessage());
    }
}