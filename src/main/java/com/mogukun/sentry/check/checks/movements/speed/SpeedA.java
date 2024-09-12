package com.mogukun.sentry.check.checks.movements.speed;

import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.MovementData;
import com.mogukun.sentry.utils.PlayerDataUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Speed (A)",
        path = "movement.speed.a",
        description = "Simple Speed Check",
        category = Category.MOVEMENT
)
public class SpeedA extends Check {

    double balance = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( isBypass() || data.sinceVehicleTick <= 5 || data.teleportTick <= 5 || data.respawnTick <= 5 ) return;

        PlayerDataUtil dataUtil = new PlayerDataUtil(data.player);

        double maxDeltaXZ = data.lastDeltaXZ * 1.8;
        maxDeltaXZ += 0.13 * dataUtil.getAmplifier(PotionEffectType.SPEED) * 0.2F;

        if ( data.currentDeltaXZ > maxDeltaXZ ) {

            balance += (int) data.currentDeltaXZ * 10;
            balance -= 1;


            if ( balance > 5 ) {
                balance = 0;
                flag(data.currentDeltaXZ + " > " + maxDeltaXZ);
            }
        }
    }

}
