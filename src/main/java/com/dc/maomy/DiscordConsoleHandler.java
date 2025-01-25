package com.dc.maomy;

import cn.nukkit.plugin.PluginBase;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

public class DiscordConsoleHandler {

    private final PluginBase plugin;
    private final DiscordApi discordApi;
    private final ConfigManager configManager;

    public DiscordConsoleHandler(PluginBase plugin, DiscordApi discordApi, ConfigManager configManager) {
        this.plugin = plugin;
        this.discordApi = discordApi;
        this.configManager = configManager;
    }

    public void setupListener() {
        discordApi.addMessageCreateListener(this::handleConsoleCommand);
    }

    private void handleConsoleCommand(MessageCreateEvent event) {
        // 檢查訊息是否來自機器人自己
        if (event.getMessageAuthor().isBotUser()) {
            return;
        }

        String consoleChannelId = configManager.getConsoleChannelId();
        if (consoleChannelId.isEmpty() || !event.getChannel().getIdAsString().equals(consoleChannelId)) {
            return;
        }

        String consoleRole = configManager.getConsoleRole();
        if (!consoleRole.isEmpty() && event.getMessageAuthor().asUser().isPresent()) {
            boolean hasRole = event.getServer().flatMap(server -> server.getRolesByName(consoleRole).stream().findFirst())
                    .map(role -> event.getMessageAuthor().asUser().get().getRoles(event.getServer().get()).contains(role))
                    .orElse(false);

            if (!hasRole) {
                event.getChannel().sendMessage("你沒有權限執行此指令！");
                return;
            }
        }

        String command = event.getMessageContent();
        if (configManager.isLogConsoleCommandsEnabled()) {
            plugin.getLogger().info("[Discord 控制台] " + event.getMessageAuthor().getName() + " 執行了指令: " + command);
        }

        plugin.getServer().getScheduler().scheduleTask(plugin, () -> {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
        });

        if (configManager.isConsoleStatusMessagesEnabled()) {
            event.getChannel().sendMessage("指令已執行: " + command);
        }
    }
}