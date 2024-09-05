package com.mogukun.sentry.check.checks.players.groundspoof;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;

@CheckInfo(
        name = "GroundSpoof (C)",
        description = "Ground Spoof Check",
        category = Category.PLAYER
)
public class GroundSpoofC extends Check {

    int buffer = 0;

    @Override
    public void handle(MovementData data)
    {

        if ( data.serverAirTick > 15 &&
               data.clientGroundTick > 15 ) {
            if (buffer++ > 5) {
                flag("sat=" + data.serverAirTick + " cgt=" + data.clientGroundTick);
                buffer = 0;
            }
        } else {
            buffer -= buffer > 0 ? 1 : 0;
        }
    }

}
