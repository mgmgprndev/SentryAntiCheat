package com.mogukun.sentry.check;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.checks.combats.aura.AuraA;
import com.mogukun.sentry.check.checks.combats.aura.AuraB;
import com.mogukun.sentry.check.checks.combats.aura.AuraC;
import com.mogukun.sentry.check.checks.combats.autoclicker.AutoClickerA;
import com.mogukun.sentry.check.checks.combats.blockhit.BlockHitA;
import com.mogukun.sentry.check.checks.combats.reach.ReachA;
import com.mogukun.sentry.check.checks.movements.fly.*;
import com.mogukun.sentry.check.checks.movements.motion.MotionA;
import com.mogukun.sentry.check.checks.movements.speed.*;
import com.mogukun.sentry.check.checks.movements.wallclimb.WallClimbA;
import com.mogukun.sentry.check.checks.movements.waterwalk.WaterWalkA;
import com.mogukun.sentry.check.checks.players.badpackets.*;
import com.mogukun.sentry.check.checks.players.groundspoof.GroundSpoofA;
import com.mogukun.sentry.check.checks.players.groundspoof.GroundSpoofB;
import com.mogukun.sentry.check.checks.players.groundspoof.GroundSpoofC;
import com.mogukun.sentry.check.checks.players.inventory.InventoryA;
import com.mogukun.sentry.check.checks.players.noslow.NoSlowFood;
import com.mogukun.sentry.check.checks.players.noslow.NoSlowSword;
import com.mogukun.sentry.check.checks.players.timer.TimerA;
import com.mogukun.sentry.check.checks.players.timer.TimerB;
import com.mogukun.sentry.check.checks.players.timer.TimerC;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CheckManager {


    public ConcurrentLinkedDeque<ViolationData> vl = new ConcurrentLinkedDeque<>();

    public ArrayList<Check> checks = new ArrayList<>();

    public CheckManager(){

        checks.add( new AuraA() );
        checks.add( new AuraB() );
        checks.add( new AuraC() );

        checks.add( new BlockHitA() );

        checks.add( new AutoClickerA() );
        checks.add( new ReachA() );

        checks.add( new FlyA1() );
        checks.add( new FlyA2() );
        checks.add( new FlyB() );
        checks.add( new FlyC() );
        checks.add( new FlyD() );
        checks.add( new FlyE() );

        checks.add( new SpeedA() );
        checks.add( new SpeedB() );
        checks.add( new SpeedC() );
        checks.add( new SpeedD() );
        checks.add( new SpeedE() );
        checks.add( new SpeedF() );


        checks.add( new MotionA() );


        checks.add( new WaterWalkA() );
        checks.add( new WallClimbA() );

        checks.add( new GroundSpoofA() );
        checks.add( new GroundSpoofB() );
        checks.add( new GroundSpoofC() );

        // A,B WILL BE DELETED FOR : TOO MANY FALSES, USELESS
        // checks.add( new TimerA() );
        // checks.add( new TimerB() );
        checks.add( new TimerC() );

        checks.add( new BadPacketA() );
        checks.add( new BadPacketB() );
        checks.add( new BadPacketC() );
        checks.add( new BadPacketD() );
        checks.add( new BadPacketE() );
        checks.add( new BadPacketF() );

        checks.add( new InventoryA() );
        checks.add( new NoSlowFood() );
        checks.add( new NoSlowSword() );
    }

    public HashMap<UUID,ArrayList<Check>> checkMap = new HashMap<>();

    private ArrayList<Check> init(Player player) {
        UUID uuid = player.getUniqueId();

        if ( checkMap.get(uuid) == null ) {
            ArrayList<Check> tempCheck = new ArrayList<>();
            for ( Check t : new ArrayList<>(checks) ) {
                tempCheck.add(t.clone().setPlayer(player));
            }
            checkMap.put(uuid, tempCheck);
        }

        return checkMap.get(uuid);
    }

    public void runCheck(Player player, Packet packet) {

        if ( packet instanceof PacketPlayInTransaction ) {
            new PlayerDataUtil(player).onTransaction();
            Sentry.instance.dataManager.getPlayerData(player).transactionReceived = System.currentTimeMillis();
        }

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
