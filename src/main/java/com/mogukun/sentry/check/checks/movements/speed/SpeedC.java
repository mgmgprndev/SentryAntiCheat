package com.mogukun.sentry.check.checks.movements.speed;

import com.mogukun.sentry.check.*;

@CheckInfo(
        name = "Speed (C)",
        description = "Horizontal Speed",
        category = Category.MOVEMENT
)
public class SpeedC extends Check {

    double buffer = 0;

    double lastDiff = Double.MAX_VALUE;

    double lastAccel = Double.MAX_VALUE;


    @Override
    public void handle(MovementData data)
    {
        if ( data.sinceVehicleTick <= 5 || data.sinceWaterTick <= 5 || data.teleportTick <= 5 || data.respawnTick <= 5 ) {
            buffer = 0;
            return;
        }

        double diff = roundTo(data.currentDeltaXZ, 7) * 10000000;
        if ( lastDiff == Double.MAX_VALUE ) {
            lastDiff = diff;
            return;
        }

        double accel = lastDiff - diff;

        if ( accel > 9000 ) {

            if ( lastAccel == Double.MAX_VALUE ) {
                lastDiff = diff;
                lastAccel = accel;
                return;
            }

            double accelDiff = accel - lastAccel;
            if ( accelDiff > 5000000 ) {
                buffer += accelDiff / 5000000;
                if ( buffer > 2.5 ) {

                    flag("buffer=" + buffer + " ad=" + accelDiff + " a=" + accel);

                    buffer = 0;
                }
            } else {
                buffer -= buffer > 0 ? 0.5 : 0;
            }

        } else {
            buffer -= buffer > 0 ? 0.5 : 0;
        }




        lastDiff = diff;
        lastAccel = accel;

    }

    private double roundTo(double n, int r) {
        return Math.round(n * Math.pow(10, r)) / Math.pow(10, r);
    }


}
