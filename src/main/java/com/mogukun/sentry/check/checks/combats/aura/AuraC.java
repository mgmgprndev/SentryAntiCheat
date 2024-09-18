package com.mogukun.sentry.check.checks.combats.aura;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.Counter;
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

    Counter armCounter = new Counter();
    Counter attackCounter = new Counter();

    @Override
    public void handle(Packet packet) {
        if ( packet instanceof PacketPlayInArmAnimation) {
            if ( getPlayerData().isDigging ) return;
            armCounter.count();
        }
        if ( packet instanceof PacketPlayInUseEntity ) {

            if ( ((PacketPlayInUseEntity) packet).a() != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK ) return;

            attackCounter.count();

            int difference = attackCounter.count() - armCounter.getCount();

            //debug("difference=" + difference);

        }
    }

}
