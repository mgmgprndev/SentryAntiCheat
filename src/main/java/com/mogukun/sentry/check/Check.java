package com.mogukun.sentry.check;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.util.AlertUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class Check implements Listener {

    public CheckInfo checkInfo;
    Player player;

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
        new AlertUtil(player, checkInfo, debug);
    }


    public void event(Event event) {}


}
