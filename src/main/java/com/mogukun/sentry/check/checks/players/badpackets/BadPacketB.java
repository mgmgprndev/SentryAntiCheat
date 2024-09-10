package com.mogukun.sentry.check.checks.players.badpackets;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;

@CheckInfo(
        name = "BadPacket (B)",
        path = "player.badpacket.b",
        description = "Invalid Rotating Packet",
        category = Category.PLAYER
)
public class BadPacketB extends Check {

    float lastYaw, lastPitch;
    boolean ignore = false;

    // Credit @DerRedstoner, from CheatGuard.

    @Override
    public void handle(Packet packet) {
        if ( packet instanceof PacketPlayInFlying) {
            PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            float yaw = flying.d();
            float pitch = flying.e();

            if ( !flying.g() && flying.h() ) {
                if ( yaw == lastYaw && pitch == lastPitch ) {
                    if(!ignore) flag();
                }

                lastYaw = yaw;
                lastPitch = pitch;
                ignore = false;
            } else ignore = true;
        } else if ( packet instanceof PacketPlayInSteerVehicle ) {
            ignore = true;
        }
    }

}
