package com.mogukun.sentry.check.checks.movements.speed;

import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.CollidingType;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Speed (E)",
        path = "movement.speed.e",
        description = "Minecraft Traffic Law Enforcement (v.1)",
        category = Category.MOVEMENT
)
public class SpeedE extends Check {

    double buffer = 0;
    int movingTick = 0;


    @Override
    public void handle(MovementData data)
    {
        if ( data.currentX != data.lastX || data.currentZ != data.lastZ ) {
            movingTick++;
        } else if ( data.currentX == data.lastX && data.currentZ == data.lastZ ) {
            movingTick = 0;
        }

        if ( isBypass() || !data.moving || data.sinceVehicleTick <= 5 || data.teleportTick <= 5 || data.respawnTick <= 5 ) {
            movingTick = 0;
            buffer = 0;
            return;
        }

        if ( movingTick < 5 ) return;

        double friction = 0.6;
        if ( data.iceTick > 0 ) friction = 0.989;
        if ( data.slimeTick > 0 ) friction = 0.8;

        double prediction = data.lastDeltaXZ * 0.91F;
        if ( data.serverAirTick > 0 && data.serverAirTick < 3 ) prediction += 0.2;
        if ( data.serverGroundTick > 4 ) prediction *= friction;
        if ( player.getWalkSpeed() > 0.2 ) prediction += (player.getWalkSpeed() - 0.2F) * 1.6F;
        int speedAmp = getPlayerUtil().getAmplifier(PotionEffectType.SPEED);
        if ( speedAmp  > 0 ) prediction += 0.20000000298023224 * speedAmp;
        prediction += 0.15;

        double diff = Math.abs(data.currentDeltaXZ - prediction);

        if ( data.currentDeltaXZ > prediction ) flag(data.currentDeltaXZ + " > " + prediction + " diff=" + diff);


    }

}
