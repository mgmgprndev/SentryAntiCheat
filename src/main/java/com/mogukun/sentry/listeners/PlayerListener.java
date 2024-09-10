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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

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
    public void onQuit(PlayerQuitEvent event) {
        Sentry.instance.checkManager.checkMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        call(player, event);
        Sentry.instance.dataManager.getPlayerData(player).teleportTick = 0;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player) {
            call(((Player) event.getDamager()), event);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        call(event.getPlayer(), event);
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event){
        call( (Player) event.getEntity(), event);
    }


    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        call((Player) event.getWhoClicked(), event);
        Sentry.instance.guiManager.callEvent(event);
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event){
        Player player = event.getPlayer();
        call(player, event);
        Sentry.instance.dataManager.getPlayerData(player).sinceVelocityTakenTick = 0;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        call(event.getEntity(), event);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        call(player, event);
        Sentry.instance.dataManager.getPlayerData(player).respawnTick = 0;
    }


    private void call(Player player, Event event) {
        Sentry.instance.checkManager.runEvent(player, event);
    }

}
