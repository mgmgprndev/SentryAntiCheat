package com.mogukun.sentry.check.checks.players.groundspoof;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;

@CheckInfo(
        name = "GroundSpoof (C)",
        path = "player.groundspoof.c",
        description = "Ground Spoof Check",
        category = Category.PLAYER
)
public class GroundSpoofC extends Check {

    int buffer = 0;

    @Override
    public void handle(MovementData data)
    {

        if ( data.sinceStandingOnBoatTick <= 2 ) return;
        if ( data.sinceVehicleTick <= 2 ) return;

        if ( data.serverAirTick > 15 &&
               data.clientGroundTick > 15 ) {
            if (buffer++ > config.getIntegerOrDefault("flag_buffer", 5)) {
                flag("sat=" + data.serverAirTick + " cgt=" + data.clientGroundTick);
                buffer = 0;
            }
        } else {
            buffer -= buffer > 0 ? 1 : 0;
        }
    }

}
