package com.mogukun.sentry.check.checks.players.badpackets;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;

@CheckInfo(
        name = "BadPacket (F)",
        description = "Sprinting Packet Flaw",
        category = Category.PLAYER
)
public class BadPacketF extends Check {

    boolean ignore = false;

    @Override
    public void handle(Packet thePacket) {
        if ( thePacket instanceof PacketPlayInFlying) {
            ignore = true;
        }
        if ( thePacket instanceof PacketPlayInEntityAction ) {
            PacketPlayInEntityAction packet = (PacketPlayInEntityAction) thePacket;
            PacketPlayInEntityAction.EnumPlayerAction action = packet.b();

            if ( action == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING
                    || action == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING )
            {

                if ( !ignore ) {
                    // debug("action=" + action); testing
                }

                ignore = false;
            }
        }
    }

}
