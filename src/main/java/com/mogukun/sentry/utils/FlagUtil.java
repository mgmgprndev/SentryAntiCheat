package com.mogukun.sentry.utils;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.events.SentryFlagEvent;
import com.mogukun.sentry.models.ViolationData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FlagUtil {


    public FlagUtil(Player player, CheckInfo checkInfo, String debug) {

        if ( !Sentry.instance.checkUtil.isEnabled(checkInfo.path()) ) return;

        UUID uuid = player.getUniqueId();

        Sentry.instance.checkManager.vl.add( new ViolationData( uuid, checkInfo.name() ) );

        int total = 0;
        int perCheck = 0;

        for( ViolationData violationData : Sentry.instance.checkManager.vl )
        {
            if ( !violationData.uuid.equals(uuid) ) continue;
            if ( violationData.check.equals(checkInfo.name()) ) perCheck++;
            total++;
        }

        String message = ChatColor.translateAlternateColorCodes('&',
                "&8&l[&6&lSENTRY&8&l]&c " + player.getName() + "&7 failed&c " + checkInfo.name() + (checkInfo.experimental() ? " (EXP)" : "" ) );

        String information = "&8&l[&6&lSENTRY&8&l]&r\n";
        information += "&fAbout This Check:&7 " + checkInfo.description() + "\n\n";
        information += "&fDebug:&7 " + (debug.isEmpty() ? "N/A" : debug) + "\n";
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

            UUID uid = op.getUniqueId();

            boolean send = false;

            Sentry.instance.alertStatus.putIfAbsent(uid, 0);

            if ( op.hasPermission("sentry.admin") ) {
                if ( Sentry.instance.alertStatus.get(uid) == 1 ) send = true;
            }

            if ( Sentry.instance.alertStatus.get(uid) == 2 &&  uid == player.getUniqueId() ) send = true;

            if ( Sentry.instance.testServer ) {
                if ( uid == player.getUniqueId() ) send = true;
            }

            if ( send ) op.spigot().sendMessage(tc);
        }

        Bukkit.getPluginManager().callEvent(new SentryFlagEvent(checkInfo, total, perCheck, player, debug));
    }


}
