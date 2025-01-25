package com.dc.maomy;

import cn.nukkit.plugin.PluginBase;
import org.javacord.api.DiscordApi;

import java.util.concurrent.CompletableFuture;

public class ServerEventHandler {

    private final PluginBase plugin;
    private final DiscordApi discordApi;
    private final ConfigManager configManager;

    public ServerEventHandler(PluginBase plugin, DiscordApi discordApi, ConfigManager configManager) {
        this.plugin = plugin;
        this.discordApi = discordApi;
        this.configManager = configManager;
    }

    public void onServerStart() {
        if (configManager.isStartMessagesEnabled()) {
            sendToDiscord(configManager.getServerStartMessage());
        }
    }

    public CompletableFuture<Void> onServerStop() {
        if (configManager.isStopMessagesEnabled()) {
            return sendToDiscord(configManager.getServerStopMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Void> sendToDiscord(String message) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            discordApi.getTextChannelById(configManager.getChannelId()).ifPresent(channel -> {
                channel.sendMessage(message)
                        .thenAccept(msg -> {
                            plugin.getLogger().info("訊息已成功發送到 Discord: " + message);
                            future.complete(null);
                        })
                        .exceptionally(e -> {
                            plugin.getLogger().error("無法發送訊息到 Discord: " + e.getMessage());
                            future.completeExceptionally(e);
                            return null;
                        });
            });
        } catch (Exception e) {
            plugin.getLogger().error("發送訊息到 Discord 時發生錯誤: " + e.getMessage());
            future.completeExceptionally(e);
        }
        return future;
    }
}