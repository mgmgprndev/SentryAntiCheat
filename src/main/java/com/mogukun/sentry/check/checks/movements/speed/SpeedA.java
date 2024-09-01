package com.mogukun.sentry.check.checks.movements.speed;

import com.mogukun.sentry.check.*;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Speed (A)",
        description = "Simple Speed Check",
        category = Category.MOVEMENT
)
public class SpeedA extends Check {

    double balance = 0;

    @Override
    public void handle(MovementData data)
    {

        PlayerDataUtil dataUtil = new PlayerDataUtil(data.player);

        double maxDeltaXZ = data.lastDeltaXZ * 1.8;
        maxDeltaXZ += dataUtil.getAmplifier(PotionEffectType.SPEED) * 0.1F;

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
