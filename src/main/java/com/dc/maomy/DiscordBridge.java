package com.dc.maomy;

import cn.nukkit.plugin.PluginBase;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

import java.util.concurrent.CompletableFuture;

public class DiscordBridge extends PluginBase {

    private DiscordApi discordApi;
    private ConfigManager configManager;
    private DiscordMessageHandler discordMessageHandler;
    private PlayerEventHandler playerEventHandler;
    private ServerEventHandler serverEventHandler;
    private ServerConsoleHandler serverConsoleHandler;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        if (!configManager.loadConfig()) {
            getLogger().error("無法加載配置文件，插件已停用！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initializeDiscordApi().thenAccept(api -> {
            discordApi = api;
            getLogger().info("Discord 機器人已成功登入！");

            initializeHandlers();

            getServer().getPluginManager().registerEvents(playerEventHandler, this);

            discordMessageHandler.setupListeners();

            serverConsoleHandler.setupConsoleLogListener();

            serverEventHandler.onServerStart();

            registerShutdownHook();

            getLogger().info("DiscordBridge 已啟用！");
        }).exceptionally(e -> {
            getLogger().error("無法初始化 Discord API: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return null;
        });
    }

    @Override
    public void onDisable() {
        if (discordApi != null) {
            serverEventHandler.onServerStop()
                    .thenRun(() -> {
                        discordApi.disconnect();
                        getLogger().info("Discord 機器人已斷開連接！");
                    })
                    .exceptionally(e -> {
                        getLogger().error("發送伺服器關閉訊息時發生錯誤: " + e.getMessage());
                        return null;
                    });
        }
        getLogger().info("DiscordBridge 已停用！");
    }

    private CompletableFuture<DiscordApi> initializeDiscordApi() {
        return new DiscordApiBuilder()
                .setToken(configManager.getBotToken())
                .addIntents(Intent.MESSAGE_CONTENT)
                .login()
                .toCompletableFuture();
    }

    private void initializeHandlers() {
        discordMessageHandler = new DiscordMessageHandler(this, discordApi, configManager);
        playerEventHandler = new PlayerEventHandler(this, discordApi, configManager);
        serverEventHandler = new ServerEventHandler(this, discordApi, configManager);
        serverConsoleHandler = new ServerConsoleHandler(this, discordApi, configManager);

        getServer().getPluginManager().registerEvents(serverConsoleHandler, this);
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (discordApi != null) {
                serverEventHandler.onServerStop()
                        .thenRun(() -> {
                            discordApi.disconnect();
                            getLogger().info("Discord 機器人已斷開連接（通過關閉鉤子）！");
                        })
                        .exceptionally(e -> {
                            getLogger().error("發送伺服器關閉訊息時發生錯誤: " + e.getMessage());
                            return null;
                        });
            }
        }));
    }
}