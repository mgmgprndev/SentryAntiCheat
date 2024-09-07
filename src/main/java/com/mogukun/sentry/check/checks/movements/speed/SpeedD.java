package com.mogukun.sentry.check.checks.movements.speed;

import com.mogukun.sentry.check.*;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Speed (D)",
        description = "Unusual XZ Speed Detection",
        category = Category.MOVEMENT
)
public class SpeedD extends Check {

    double balance = 0;
    int speedLevel = -1;
    int sinceLostSpeedLevel = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( !data.moving || data.sinceVehicleTick <= 5 || data.sinceWaterTick <= 5 || data.teleportTick <= 5 || data.respawnTick <= 5 ) {
            balance = 0;
            return;
        }

        balance += data.currentDeltaXZ;
        balance -= data.lastDeltaXZ;


        double decupleBalance = balance * 10;

        int spdLevel = new PlayerDataUtil(player).getAmplifier(PotionEffectType.SPEED);

        // this is because, right after the effect expired, this will cause some false flags. so i did this.
        sinceLostSpeedLevel++;
        if ( spdLevel != -1 ) {
            sinceLostSpeedLevel = 0;
            speedLevel = spdLevel;
        }
        // now, new speed level can set.
        if ( sinceLostSpeedLevel >= 5 ) {
            speedLevel = -1;
        }

        double maxBalance = ( data.currentDeltaY == 0 ? 3.4 : 7.2 );

        if ( data.currentDeltaY == 0 && data.isHittingHead ) {
            maxBalance += 1.5;
        }

        if ( data.serverGroundTick <= 2 ) {
            maxBalance += 0.8;
        }

        if ( speedLevel != -1 ) {
            maxBalance += (speedLevel * 1.2);
        }

        if ( data.sinceOnStairTick < 5 ) {
            maxBalance += 0.8;
        }

        if ( decupleBalance > maxBalance ) {
            flag( decupleBalance + ">" + maxBalance );
        }

    }

}
