package com.dc.maomy;

import cn.nukkit.plugin.PluginBase;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

public class DiscordToMinecraftHandler {

    private final PluginBase plugin;
    private final DiscordApi discordApi;
    private final ConfigManager configManager;

    public DiscordToMinecraftHandler(PluginBase plugin, DiscordApi discordApi, ConfigManager configManager) {
        this.plugin = plugin;
        this.discordApi = discordApi;
        this.configManager = configManager;
    }

    public void setupListener() {
        discordApi.addMessageCreateListener(event -> {
            if (!event.getMessageAuthor().isBotUser()) {
                String message = configManager.getDiscordToMinecraftMessageFormat()
                        .replace("{username}", event.getMessageAuthor().getName())
                        .replace("{message}", event.getMessageContent());
                plugin.getServer().broadcastMessage(message);
            }
        });
    }
}