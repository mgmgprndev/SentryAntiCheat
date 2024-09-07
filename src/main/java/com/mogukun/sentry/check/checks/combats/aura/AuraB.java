package com.mogukun.sentry.check.checks.combats.aura;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

@CheckInfo(
        name = "Aura (B)",
        description = "Badly Coded KillAura detection",
        category = Category.COMBAT
)
public class AuraB extends Check {

    long lastFlyingPacket = 0;

    @Override
    public void handle(Packet packet) {
        long now = System.currentTimeMillis();
        if ( packet instanceof PacketPlayInFlying ) {
            lastFlyingPacket = now;
        }
        if ( packet instanceof PacketPlayInUseEntity ) {
            long diff = now - lastFlyingPacket;
            if ( diff < 5 ) flag("diff=" + diff);
        }
    }

}
