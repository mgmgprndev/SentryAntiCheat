package com.mogukun.sentry.check.checks.players.groundspoof;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;

@CheckInfo(
        name = "GroundSpoof (B)",
        path = "player.groundspoof.b",
        description = "Ground Spoof Check",
        category = Category.PLAYER
)
public class GroundSpoofB extends Check {

    int buffer = 0;

    @Override
    public void handle(MovementData data)
    {

        if ( data.sinceStandingOnBoatTick <= 2 ) return;
        if ( data.sinceVehicleTick <= 2 ) return;


        if ( data.serverGroundTick > 15 &&
               data.clientAirTick > 15 ) {
            if (buffer++ > 5) {
                flag("sgt=" + data.serverGroundTick + " sat=" + data.clientAirTick);
                buffer = 0;
            }
        } else {
            buffer -= buffer > 0 ? 1 : 0;
        }
    }

}
