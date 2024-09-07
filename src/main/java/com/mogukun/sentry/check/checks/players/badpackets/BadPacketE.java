package com.mogukun.sentry.check.checks.players.badpackets;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

@CheckInfo(
        name = "BadPacket (E)",
        description = "Rotating Without Rotating",
        category = Category.PLAYER
)
public class BadPacketE extends Check {

    float lastYaw = 0, lastPitch = 0F;
    boolean ignore = false;

    @Override
    public void handle(Packet thePacket) {
        if ( thePacket instanceof PacketPlayInFlying) {
            PacketPlayInFlying packet = (PacketPlayInFlying) thePacket;

            if ( packet.h() && !packet.g() ) {

                float yaw = packet.d();
                float pitch = packet.e();

                if ( !ignore && yaw == lastYaw && pitch == lastPitch ) {
                    flag();
                }

                lastYaw = yaw;
                lastPitch = pitch;
                ignore = false;
            } else {
                ignore = true;
            }
        }
        if ( thePacket instanceof PacketPlayInSteerVehicle ) {
            ignore = true;
        }
    }

}
