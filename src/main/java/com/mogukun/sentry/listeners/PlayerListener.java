package com.mogukun.sentry.listeners;

import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CraftPlayer craftPlayer = (CraftPlayer) event.getPlayer();
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        NetworkManager networkManager = entityPlayer.playerConnection.networkManager;
        ChannelPipeline pipeline = networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", "sentry_packet_handler", new PacketListener(event.getPlayer()));
    }

}
