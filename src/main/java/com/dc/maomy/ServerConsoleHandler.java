package com.dc.maomy;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.event.EventHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ServerConsoleHandler implements Listener {

    private final PluginBase plugin;
    private final DiscordApi discordApi;
    private final ConfigManager configManager;

    public ServerConsoleHandler(PluginBase plugin, DiscordApi discordApi, ConfigManager configManager) {
        this.plugin = plugin;
        this.discordApi = discordApi;
        this.configManager = configManager;
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        sendToDiscord("控制台指令: " + command);
    }

    public void setupConsoleLogListener() {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8) {
            @Override
            public void println(String x) {
                originalOut.println(x);
                super.println(x);
            }

            @Override
            public void print(String x) {
                originalOut.print(x);
                super.print(x);
            }
        };

        System.setOut(printStream);
        System.setErr(printStream);

        new Thread(() -> {
            while (true) {
                String output = outputStream.toString(StandardCharsets.UTF_8);
                if (!output.isEmpty()) {
                    sendToDiscord("控制台日誌: " + output);
                    outputStream.reset();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }));
    }

    private void sendToDiscord(String message) {
        String consoleChannelId = configManager.getConsoleChannelId();
        if (consoleChannelId.isEmpty()) {
            return;
        }

        discordApi.getTextChannelById(consoleChannelId).ifPresent(channel -> {
            channel.sendMessage(message);
        });
    }
}