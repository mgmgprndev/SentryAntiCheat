package com.mogukun.sentry.listeners;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.utils.PlayerPacketUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Sentry.instance.playerPacketUtil.addPlayer(event.getPlayer());
        call(event.getPlayer(), event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Sentry.instance.checkManager.checkMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        call(player, event);
        Sentry.instance.dataManager.getPlayerData(player).lastPlace = System.currentTimeMillis();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
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
            Player player = ((Player) event.getDamager());
            call(player, event);
            Sentry.instance.dataManager.getPlayerData(player).isDigging = false;
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
        Sentry.instance.dataManager.getPlayerData(player).velocityTaken = event.getVelocity();
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
