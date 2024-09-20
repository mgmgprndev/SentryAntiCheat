package com.mogukun.sentry.utils;

import com.mogukun.sentry.listeners.PacketHandler;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerPacketUtil {

    private ConcurrentHashMap<UUID,ExecutorService> services = new ConcurrentHashMap<>();

    public void addPlayer(Player player) {

        ExecutorService s = Executors.newSingleThreadExecutor();
        s.execute(()-> getPipeline(player).addBefore("packet_handler", "sentry_packet_handler", new PacketHandler(player)));
        services.put(player.getUniqueId(), s);
    }

    public void clear() {
        for ( UUID uuid : services.keySet() ) {
            Player player = Bukkit.getPlayer(uuid);
            if ( player != null && player.isOnline() ) getPipeline(player).remove("sentry_packet_handler");
            services.get(uuid).shutdown();
        }
    }

    private ChannelPipeline getPipeline(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        NetworkManager networkManager = entityPlayer.playerConnection.networkManager;
        return networkManager.channel.pipeline();
    }

}
