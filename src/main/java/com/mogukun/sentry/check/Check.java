package com.mogukun.sentry.check;

import com.mogukun.sentry.Sentry;
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
        flag("N/A");
    }

    public void flag(String debug){
        UUID uuid = player.getUniqueId();

        Sentry.instance.checkManager.vl.add( new ViolationData( uuid, checkInfo.name() ));

        int total = 0;
        int perCheck = 0;

        for( ViolationData violationData : Sentry.instance.checkManager.vl )
        {
            if ( !violationData.uuid.equals(uuid) ) continue;
            if ( violationData.check.equals(checkInfo.name()) ) perCheck++;
            total++;
        }

        String message = ChatColor.translateAlternateColorCodes('&',
                "&8&l[&6&lSENTRY&8&l]&c " + player.getName() + "&7 failed&c " + checkInfo.name());

        String information = "&8&l[&6&lSENTRY&8&l]&r\n";
        information += "&fAbout This Check:&7 " + checkInfo.description() + "\n\n";
        information += "&fDebug:&7 " + debug + "\n";
        information += "&fVL\n";
        information += "&f* This Check:&7 " + perCheck + "\n";
        information += "&f* Total     :&7 " + total    + "\n\n";
        information += "&6Click To Teleport";

        TextComponent tc = new TextComponent(message);

        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                ChatColor.translateAlternateColorCodes('&',
                        information) ).create()));

        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/tp " + player.getName() ));


        for (Player op : Bukkit.getOnlinePlayers()) {
            if ( op.hasPermission("sentry.flag") ) op.spigot().sendMessage(tc);
        }
    }


    public void event(Event event) {}


}
