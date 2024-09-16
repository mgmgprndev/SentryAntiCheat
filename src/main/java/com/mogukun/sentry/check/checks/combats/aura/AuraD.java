package com.mogukun.sentry.check.checks.combats.aura;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.*;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

@CheckInfo(
        name = "KillAura (D)",
        path = "combat.killaura.d",
        description = "Poorly Coded KillAura Detection",
        category = Category.COMBAT
)
public class AuraD extends Check {

    BetterCounter counter = new BetterCounter();

    int lastRotatingTick = 0;
    double totalYawDelta = 0;

    long lastAttack = Long.MAX_VALUE;

    @Override
    public void handle(MovementData data) {
        if ( isBypass() )  {
            counter.clear();
            return;
        }

        if ( (System.currentTimeMillis() - lastAttack) > 500 ) {
            return;
        }

        if ( data.rotating ) {
            totalYawDelta += data.currentDeltaYaw;
        } else {

            if ( totalYawDelta != 0 && lastRotatingTick != 0 ) {
                if ( totalYawDelta > 1 && lastRotatingTick < 2 ) counter.count(totalYawDelta);
            }

            totalYawDelta = 0;
        }


        double count = counter.getCount();
        if ( count > 50 ) flag(Double.toString(count));


        lastRotatingTick = data.rotatingTick;
    }

    @Override
    public void handle(Packet packet) {
        long now = System.currentTimeMillis();
        if ( packet instanceof PacketPlayInUseEntity) {
            if (((PacketPlayInUseEntity) packet).a() != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) return;
            lastAttack = now;
        }
    }
}
