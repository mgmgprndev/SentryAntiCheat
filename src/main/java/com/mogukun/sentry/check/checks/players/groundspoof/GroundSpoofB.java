package com.mogukun.sentry.check.checks.players.groundspoof;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;

@CheckInfo(
        name = "GroundSpoof (B)",
        description = "Ground Spoof Check",
        category = Category.PLAYER
)
public class GroundSpoofB extends Check {

    int buffer = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( data.serverGroundTick > 15 &&
               data.serverAirTick > 15 ) {
            if (buffer++ > 5) {
                flag("sgt=" + data.serverGroundTick + " sat=" + data.serverAirTick);
                buffer = 0;
            }
        } else {
            buffer -= buffer > 0 ? 1 : 0;
        }
    }

}
