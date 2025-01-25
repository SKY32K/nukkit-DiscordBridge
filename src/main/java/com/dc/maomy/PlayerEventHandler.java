package com.dc.maomy;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.plugin.PluginBase;
import org.javacord.api.DiscordApi;

public class PlayerEventHandler implements Listener {

    private final PluginBase plugin;
    private final DiscordApi discordApi;
    private final ConfigManager configManager;

    public PlayerEventHandler(PluginBase plugin, DiscordApi discordApi, ConfigManager configManager) {
        this.plugin = plugin;
        this.discordApi = discordApi;
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (configManager.isMinecraftToDiscordEnabled()) {
            String message = configManager.getMinecraftToDiscordMessageFormat()
                    .replace("{username}", event.getPlayer().getName())
                    .replace("{message}", event.getMessage());
            sendToDiscord(message);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (configManager.isJoinMessagesEnabled() && configManager.isMinecraftToDiscordEnabled()) {
            String message = configManager.getPlayerJoinMessageFormat()
                    .replace("{username}", event.getPlayer().getName());
            sendToDiscord(message);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (configManager.isQuitMessagesEnabled() && configManager.isMinecraftToDiscordEnabled()) {
            String message = configManager.getPlayerQuitMessageFormat()
                    .replace("{username}", event.getPlayer().getName());
            sendToDiscord(message);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (configManager.isDeathMessagesEnabled() && configManager.isMinecraftToDiscordEnabled()) {
            String message = configManager.getPlayerDeathMessageFormat()
                    .replace("{username}", event.getEntity().getName());
            sendToDiscord(message);
        }
    }

    private void sendToDiscord(String message) {
        discordApi.getTextChannelById(configManager.getChannelId()).ifPresent(channel -> {
            channel.sendMessage(message);
        });
    }
}