package com.mogukun.sentry.check.checks.movements.speed;

import com.mogukun.sentry.check.*;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Speed (B)",
        description = "Predication Speed Check",
        category = Category.MOVEMENT
)
public class SpeedB extends Check {

    double buffer = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( isBypass() || data.sinceVehicleTick <= 5 || data.sinceWaterTick <= 5 || data.teleportTick <= 5 || data.respawnTick <= 5 ) {
            buffer = 0;
            return;
        }

        PlayerDataUtil dataUtil = new PlayerDataUtil(data.player);

        float predicted = (float) data.lastDeltaXZ * 0.91F + 0.026F;
        float difference = (float) data.currentDeltaXZ - predicted;

        if ( data.serverAirTick > 1 && difference > 0.001 ) {
            if (buffer++ > 1 ) {
                flag("pred=" + predicted + " real=" + data.currentDeltaXZ + " difference=" + difference);
                buffer -= 0.5;
            }
        } else {
            buffer -= buffer > 0 ? 0.05 : 0;
        }
    }

}
