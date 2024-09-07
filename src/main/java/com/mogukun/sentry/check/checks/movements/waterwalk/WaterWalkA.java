package com.mogukun.sentry.check.checks.movements.waterwalk;

import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.Counter;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "WaterWalk (A)",
        description = "Do not walk on water, jesus!",
        category = Category.MOVEMENT
)
public class WaterWalkA extends Check {


    Counter counter = new Counter();

    @Override
    public void handle(MovementData data)
    {
        if ( isBypass() ) return;
        if (!data.isInLiquid) return;
        if (data.isHittingHead) return;
        if (data.serverGround) return;

        if ( data.currentDeltaY == data.lastDeltaY ) {
            int count = counter.count();
            if ( count > 5 ) {
                flag("count=" + count );
            } else {
                debug("count=" + count);
            }
        } else {
            counter.decrease();
        }
    }

}
