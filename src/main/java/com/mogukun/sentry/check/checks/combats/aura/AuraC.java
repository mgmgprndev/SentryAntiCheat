package com.mogukun.sentry.check.checks.combats.aura;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

@CheckInfo(
        name = "KillAura (C)",
        path = "combat.killaura.c",
        description = "No Swing KillAura Detection",
        category = Category.COMBAT
)
public class AuraC extends Check {

    long lastArm = 0;

    @Override
    public void handle(Packet packet) {
        long now = System.currentTimeMillis();
        if ( packet instanceof PacketPlayInArmAnimation) {
            lastArm = now;
        }
        if ( packet instanceof PacketPlayInUseEntity ) {

            if ( ((PacketPlayInUseEntity) packet).a() != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK ) return;

            long diff = now - lastArm;

            if ( diff > 10 ) {
                flag("diff=" + diff);
            }

        }
    }

}
