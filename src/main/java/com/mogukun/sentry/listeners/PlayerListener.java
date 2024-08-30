package com.mogukun.sentry.listeners;

import com.mogukun.sentry.Sentry;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.Executors;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        call(event.getPlayer(), event);

        CraftPlayer craftPlayer = (CraftPlayer) event.getPlayer();
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        NetworkManager networkManager = entityPlayer.playerConnection.networkManager;
        ChannelPipeline pipeline = networkManager.channel.pipeline();

        Executors.newSingleThreadExecutor().execute(()->
                        pipeline.addBefore("packet_handler", "sentry_packet_handler",
                                new PacketHandler(event.getPlayer()))
        );
    }

    @EventHandler
    public void onEvent(PlayerTeleportEvent event){
        call(event.getPlayer(), event);
    }


    private void call(Player player, Event event) {
        Sentry.instance.checkManager.runEvent(player, event);
    }

}
