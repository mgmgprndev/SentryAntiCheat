package com.mogukun.sentry.listeners;

import com.mogukun.sentry.Sentry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.UnsupportedEncodingException;

public class MessageListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        String brand = "Unknown";
        try {
            brand = new String(bytes, "UTF-8").substring(1);
        } catch (UnsupportedEncodingException ignore) {}
        Sentry.instance.dataManager.getPlayerData(player).clientBrand = brand;
        System.out.println(brand);
    }
}
