package com.mogukun.sentry.check;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.models.MovementData;
import com.mogukun.sentry.models.PlayerData;
import com.mogukun.sentry.utils.FlagUtil;
import com.mogukun.sentry.utils.PlayerDataUtil;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public abstract class Check implements Listener, Cloneable{

    public CheckInfo checkInfo;
    public Player player;

    public Check(){
        checkInfo = this.getClass().getAnnotation(CheckInfo.class);
    }

    public Check setPlayer(Player p){
        this.player = p;
        return this;
    }

    public void handle(Packet packet){}
    public void handle(MovementData data){}

    public void debug(String debug) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8&l[&6&lSENTRY&c&l DEBUGGER&8&l]&f " + debug));
    }

    public void flag() {
        flag("");
    }

    public void flag(String debug){
        new FlagUtil(player, checkInfo, debug);
    }


    public void event(Event event) {}

    public PlayerData getPlayerData() {
        return Sentry.instance.dataManager.getPlayerData(player);
    }

    public PlayerDataUtil getPlayerUtil() {
        return new PlayerDataUtil(player);
    }

    public boolean isBypass() {
        return getPlayerUtil().isBypass();
    }

    public long getPing() {
        return getPlayerData().ping;
    }

    public long getTransactionPing() {
        new PlayerDataUtil(player).runTransactionPingCheck();
        return getPlayerData().transactionPing;
    }


    @Override
    public Check clone() {
        try {
            return (Check) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

}
