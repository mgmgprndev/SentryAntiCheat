package com.mogukun.sentry.check;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.checks.movements.fly.FlyA1;
import com.mogukun.sentry.check.checks.movements.fly.FlyA2;
import com.mogukun.sentry.check.checks.movements.fly.FlyB;
import com.mogukun.sentry.check.checks.movements.speed.SpeedA;
import com.mogukun.sentry.check.checks.players.protocol.ProtocolA;
import com.mogukun.sentry.check.checks.players.timer.TimerA;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

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

        checks.add( new ProtocolA() );

        checks.add( new TimerA() );

    }

    public HashMap<UUID,ArrayList<Check>> checkMap = new HashMap<>();

    private ArrayList<Check> init(Player player) {
        UUID uuid = player.getUniqueId();

        if ( checkMap.get(uuid) == null ) {
            ArrayList<Check> tempCheck = new ArrayList<>();
            for ( Check t : (ArrayList<Check>) checks.clone() ) {
                tempCheck.add(t.setPlayer(player));
                Sentry.instance.getServer()
                        .getPluginManager().registerEvents( t , Sentry.instance );
            }
            checkMap.put(uuid, tempCheck);
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

            boolean ground = packetFlying.f();
            boolean moving = packetFlying.g();
            boolean rotating = packetFlying.h();

            double x = moving ? packetFlying.a() : lastMovementData.currentX;
            double y = moving ? packetFlying.b() : lastMovementData.currentY;
            double z = moving ? packetFlying.c() : lastMovementData.currentZ;

            float yaw = rotating ? packetFlying.d() : lastMovementData.currentYaw;
            float pitch = rotating ? packetFlying.e()  : lastMovementData.currentPitch;


            data = new MovementData(player,
                    x, y, z, // X Y Z
                    yaw, pitch, // Yaw and Pitch
                    ground, moving, rotating, // ground, moving, rotating
                    lastMovementData // movement data
            );

            Sentry.instance.dataManager.getPlayerData(player).data = data;

            if ( !hasLastMovementData ) data = null; // skip this for now

        }

        for ( Check check : init(player) )
        {
            check.handle(packet);
            if ( data != null ) check.handle(data);
        }
    }

    public void runEvent(Player player, Event event) {
        for ( Check check : init(player) )
        {
            check.event(event);
        }
    }

}
