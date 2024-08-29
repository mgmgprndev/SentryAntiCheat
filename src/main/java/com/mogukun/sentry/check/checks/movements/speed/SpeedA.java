package com.mogukun.sentry.check.checks.movements.speed;

import com.mogukun.sentry.check.*;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Speed (A)",
        description = "Simple Speed Check",
        category = Category.MOVEMENT
)
public class SpeedA extends Check {

    double buffer = 0;

    @Override
    public CheckResult handle(MovementData data)
    {

        PlayerDataUtil dataUtil = new PlayerDataUtil(data.player);

        double maxDeltaXZ = data.lastDeltaXZ * 1.8;
        maxDeltaXZ += dataUtil.getAmplifier(PotionEffectType.SPEED) * 0.1F;

        if ( data.currentDeltaXZ > maxDeltaXZ ) {
            if ( buffer++ > 5 ) {
                buffer = 0;
                return new CheckResult(data.currentDeltaXZ + " > " + maxDeltaXZ);
            }
        } else {
            buffer -= buffer > 0 ? -0.1 : 0;
        }

        return null;

    }

}
