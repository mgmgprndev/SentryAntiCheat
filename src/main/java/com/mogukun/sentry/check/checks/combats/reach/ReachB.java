package com.mogukun.sentry.check.checks.combats.reach;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import com.mogukun.sentry.models.LocationTimeStamp;
import com.mogukun.sentry.wrapper.PacketWrapper;
import com.mogukun.sentry.wrapper.WrappedPacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.*;

import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "Reach (A)",
        path = "combat.reach.b",
        description = "A Shit Reach Check",
        category = Category.COMBAT
)
public class ReachB extends Check {

    ConcurrentLinkedDeque<LocationTimeStamp> locationTimeStamps = new ConcurrentLinkedDeque<>();

    long ping = 0;

    @Override
    public void handle(MovementData data) { this.ping = data.ping; }


    @Override
    public void handle(Packet thePacket)
    {

        if ( thePacket instanceof PacketPlayOutSpawnEntity ) {
            PacketPlayOutSpawnEntity p = (PacketPlayOutSpawnEntity) thePacket;

            // track entity spawn
        }
        if ( thePacket instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove ) {
            WrappedPacketPlayOutEntity wrapper = new WrappedPacketPlayOutEntity(thePacket);
            // track move
        }
        if ( thePacket instanceof PacketPlayOutEntityDestroy ) {

            // track remove
        }
        if ( thePacket instanceof PacketPlayOutEntityTeleport ) {
            // track teleport
        }

        // the methods
        // track player's move here
        // track all entity moves here
        // track player's hitting here
        // on hit, look for the entity's moves that recoded most nearly to nowtime - ping
        // if the location found, it will called 'theEntityLocationPlayerSee'.
        // check move packet sent before the hit packet
        // difference between the lastMovePacket's location and the theEntityLocationPlayerSee.
        // this could be less 3.0 if done property and player is legit
        //
        // if ( reach > 3.0 ) flag(); now done!
        //
        // this will use 3 packets. InFlyingPacket, InUseEntityPacket, and OutRelMove
        //
    }

}
