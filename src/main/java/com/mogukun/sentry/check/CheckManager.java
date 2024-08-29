package com.mogukun.sentry.check;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.checks.movements.fly.FlyA1;
import com.mogukun.sentry.check.checks.movements.fly.FlyA2;
import com.mogukun.sentry.check.checks.movements.fly.FlyB;
import com.mogukun.sentry.check.checks.movements.speed.SpeedA;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CheckManager {


    ArrayList<ViolationData> vl = new ArrayList<>();

    ArrayList<Check> checks = new ArrayList<>();

    public CheckManager(){


        checks.add( new FlyA1() );
        checks.add( new FlyA2() );
        checks.add( new FlyB() );

        checks.add( new SpeedA() );

    }

    public HashMap<UUID,ArrayList<Check>> checkMap = new HashMap<>();

    private ArrayList<Check> init(Player player) {
        UUID uuid = player.getUniqueId();

        if ( checkMap.get(uuid) == null ) {
            checkMap.put(uuid, (ArrayList<Check>) checks.clone());
        }

        return checkMap.get(uuid);
    }

    public void runCheck(Player player, Packet packet) {

        MovementData data = null;

        if ( packet instanceof PacketPlayInFlying ) {
            PacketPlayInFlying packetFlying = (PacketPlayInFlying) packet;

            MovementData lastMovementData =  Sentry.instance.dataManager.getPlayerData(player).data;
            boolean hasLastMovementData = lastMovementData != null;

            if ( !hasLastMovementData ) {
                // for fix error, i put fake data here
                lastMovementData = new MovementData();
            }

            data = new MovementData(player,
                    packetFlying.a(), packetFlying.b(), packetFlying.c(), // X Y Z
                    packetFlying.d(), packetFlying.e(), // Yaw and Pitch
                    packetFlying.f(), packetFlying.g(), packetFlying.h(), // ground, moving, rotating
                    lastMovementData // movement data
            );

            Sentry.instance.dataManager.getPlayerData(player).data = data;

            if ( !hasLastMovementData ) data = null; // skip this for now

        }

        for ( Check check : init(player) )
        {

            UUID uuid = player.getUniqueId();
            CheckInfo info = check.checkInfo;

            ArrayList<CheckResult> results = new ArrayList<>();
            // support some way
            results.add( check.handle(packet) );
            if ( data != null ) results.add( check.handle(data) );

            for ( CheckResult result : results )
            {
                if ( result == null ) continue;


                vl.add( new ViolationData( uuid, info.name() ));

                int total = 0;
                int perCheck = 0;

                for( ViolationData violationData : vl )
                {
                    if ( !violationData.uuid.equals(uuid) ) continue;
                    if ( violationData.check.equals(info.name()) ) perCheck++;
                    total++;
                }

                String message = ChatColor.translateAlternateColorCodes('&',
                        "&8&l[&6&lSENTRY&8&l]&c " + player.getName() + "&7 failed&c " + info.name());

                String information = "&8&l[&6&lSENTRY&8&l]&r\n";
                information += "&fAbout This Check:&7 " + info.description() + "\n\n";
                information += "&fDebug:&7 " + result.debug + "\n";
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


        }
    }

}
