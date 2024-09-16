package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.BetterCounter;
import com.mogukun.sentry.models.DoubleLong;
import com.mogukun.sentry.models.MovementData;
import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "Fly (D)",
        path = "movement.fly.d",
        description = "Repeating Same DeltaY in the Air",
        category = Category.MOVEMENT
)
public class FlyD extends Check {


    ConcurrentLinkedDeque<DoubleLong> samples = new ConcurrentLinkedDeque<>();
    BetterCounter bCounter = new BetterCounter();
    BetterCounter bufferCounter = new BetterCounter();

    double lastBuffer = Double.MAX_VALUE;

    @Override
    public void handle(MovementData data)
    {

        long now = System.currentTimeMillis();

        if ( isBypass() || data.serverAirTick < 1 ||
                data.sinceVehicleTick <= 10 || data.sinceStandingOnBoatTick <= 10 ||
                data.sinceWebTick <= 10 || data.sinceWaterTick <= 10 ||
                data.sinceClimbTick <= 10 || data.teleportTick <= 10 ||
                data.sinceSlimeTick < 10 || data.sinceVelocityTaken < 10 ) {

            samples.clear();
            bCounter.clear();
            bufferCounter.clear();
            return;
        }

        double accel = Math.abs(data.currentDeltaY - data.lastDeltaY);
        if ( data.currentDeltaY > 1 && accel < 0.1 && data.currentY < data.lastY ) return;

        samples.add( new DoubleLong(floor(data.currentDeltaY), now) );
        samples.removeIf(d -> now - d.b > 1000 );

        double buffer = 0;

        for ( DoubleLong d1 : samples ) {
            for ( DoubleLong d2 : samples ) {
                if ( d1.b == d2.b ) continue;

                double diff = Math.abs(d1.a - d2.a);
                if ( diff != 0 ) {
                    buffer += (1D / 64D) / diff;
                } else {
                    buffer += 0.5;
                }

            }
        }

        double totalBuffer = bCounter.count(buffer);


        if ( lastBuffer != Double.MAX_VALUE ) {
            if ( totalBuffer > 150 && totalBuffer > lastBuffer ) {
                bufferCounter.count(totalBuffer);
            } else bufferCounter.count(-150);

            double bufferCount =  bufferCounter.getCount();
            if ( bufferCount > 0 ) {
                flag("buffer1=" + bufferCount + " buffer2=" + totalBuffer + " sampleSize=" + samples.size() );
            }
        }

        lastBuffer = totalBuffer;
    }


    private double floor(double d){
        return Math.floor(d * 1000) / 1000;
    }
}


