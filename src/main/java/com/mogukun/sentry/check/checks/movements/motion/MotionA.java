package com.mogukun.sentry.check.checks.movements.motion;

import com.mogukun.sentry.check.*;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Motion (A)",
        description = "Impossible Movement",
        category = Category.MOVEMENT
)
public class MotionA extends Check {

    int buffer = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( !data.rotating ) return;
        if ( !data.moving ) return;
        if ( data.isInClimb ) return;

        double accelXZ = Math.abs(data.currentDeltaXZ - data.lastDeltaXZ);

        if ( accelXZ < 5.0E-15 && ( data.currentDeltaXZ > 0 || data.lastDeltaXZ > 0  ) ) {
            if ( buffer++ > 2 ) {
                flag( "xz=" + accelXZ );
                buffer = 0;
            }
        } else {
            buffer -= buffer > 0 ? 1 : 0;
        }


    }

}
