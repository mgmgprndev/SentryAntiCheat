package com.mogukun.sentry.check;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.checks.combats.aura.AuraA;
import com.mogukun.sentry.check.checks.combats.autoclicker.AutoClickerA;
import com.mogukun.sentry.check.checks.combats.reach.ReachA;
import com.mogukun.sentry.check.checks.movements.fly.FlyA1;
import com.mogukun.sentry.check.checks.movements.fly.FlyA2;
import com.mogukun.sentry.check.checks.movements.fly.FlyB;
import com.mogukun.sentry.check.checks.movements.motion.MotionA;
import com.mogukun.sentry.check.checks.movements.fly.FlyC;
import com.mogukun.sentry.check.checks.movements.speed.SpeedA;
import com.mogukun.sentry.check.checks.movements.wallclimb.WallClimbA;
import com.mogukun.sentry.check.checks.movements.waterwalk.WaterWalkA;
import com.mogukun.sentry.check.checks.players.badpackets.BadPacketA;
import com.mogukun.sentry.check.checks.players.badpackets.BadPacketB;
import com.mogukun.sentry.check.checks.players.badpackets.BadPacketC;
import com.mogukun.sentry.check.checks.players.groundspoof.GroundSpoofA;
import com.mogukun.sentry.check.checks.players.groundspoof.GroundSpoofB;
import com.mogukun.sentry.check.checks.players.groundspoof.GroundSpoofC;
import com.mogukun.sentry.check.checks.players.inventory.InventoryA;
import com.mogukun.sentry.check.checks.players.noslow.NoSlowA;
import com.mogukun.sentry.check.checks.players.timer.TimerA;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayOutKeepAlive;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CheckManager {


    public ArrayList<ViolationData> vl = new ArrayList<>();

    ArrayList<Check> checks = new ArrayList<>();

    public CheckManager(){

        checks.add( new AuraA() );
        checks.add( new AutoClickerA() );
        checks.add( new ReachA() );

        checks.add( new FlyA1() );
        checks.add( new FlyA2() );
        checks.add( new FlyB() );

        checks.add( new SpeedA() );

        checks.add( new MotionA() );
        checks.add( new FlyC() );

        checks.add( new WaterWalkA() );
        checks.add( new WallClimbA() );

        checks.add( new GroundSpoofA() );
        checks.add( new GroundSpoofB() );
        checks.add( new GroundSpoofC() );

        checks.add( new TimerA() );
        //checks.add( new TimerB() );

        checks.add( new BadPacketA() );
        checks.add( new BadPacketB() );
        checks.add( new BadPacketC() );

        checks.add( new InventoryA() );
        checks.add( new NoSlowA() );
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

        if ( packet instanceof PacketPlayOutKeepAlive ) {
            Sentry.instance.dataManager.getPlayerData(player).lastOutKeepAlive = System.currentTimeMillis();
        }

        if ( packet instanceof PacketPlayInKeepAlive ) {
            Sentry.instance.dataManager.getPlayerData(player).lastInKeepAlive = System.currentTimeMillis();
            Sentry.instance.dataManager.getPlayerData(player).ping =
                    Sentry.instance.dataManager.getPlayerData(player).lastInKeepAlive - Sentry.instance.dataManager.getPlayerData(player).lastOutKeepAlive;

        }

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
