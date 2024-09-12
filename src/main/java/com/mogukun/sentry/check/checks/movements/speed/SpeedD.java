package com.mogukun.sentry.check.checks.movements.speed;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Speed (D)",
        path = "movement.speed.d",
        description = "Minecraft Traffic Law Enforcement",
        category = Category.MOVEMENT
)
public class SpeedD extends Check {

    double lastFriction = 0;
    double groundY = 0;
    int bypassTick = 0;
    int movingTick = 0;
    int sinceSprinting = 0;
    double buffer = 0;

    @Override
    public void handle(MovementData data)
    {
        bypassTick++;
        movingTick++;
        sinceSprinting++;
        if ( player.isSprinting() ) sinceSprinting = 0;

        if ( data.currentX == data.lastX && data.currentZ == data.lastZ ) {
            movingTick = 0;
            buffer = 0;
        }

        if ( movingTick < 5 ) {
            bypassTick = 0;
            buffer = 0;
        }

        if ( data.waterTick > 0 ) bypassTick = 0;

        if ( data.serverGround ) {
            groundY = data.currentY;
        }

        if ( Math.abs(groundY - data.currentY) >= 1 ) {
            bypassTick = 0;
        }

        if ( bypassTick < 5 ) return;
        if ( data.clientAirTick > 12 ) return;


        double sprintAddition = ( sinceSprinting < 5 ? 0.0263 : 0 );
        double friction = data.currentDeltaXZ / data.lastDeltaXZ - sprintAddition;
        if ( lastFriction == 0 ) {
            lastFriction = friction;
            return;
        }

        double pred = data.lastDeltaXZ * lastFriction + sprintAddition;
        int speedAmp = getPlayerUtil().getAmplifier(PotionEffectType.SPEED);
        if ( speedAmp  > 0 ) pred += 0.20000000298023224 * speedAmp;

        double diff = data.currentDeltaXZ - pred;

        double allowedMaxDiff = ( data.clientAirTick < 2 ? 1 : 0.1 );

        if ( diff > allowedMaxDiff ) {
            flag("diff=" + diff + " > " + allowedMaxDiff + " friction=" + friction);
        }


        lastFriction = friction;
    }

}
