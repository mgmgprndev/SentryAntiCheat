package com.mogukun.sentry.check.checks.players.groundspoof;

import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.MovementData;

@CheckInfo(
        name = "GroundSpoof (A)",
        path = "player.groundspoof.a",
        description = "Simple Ground Spoof Check",
        category = Category.PLAYER
)
public class GroundSpoofA extends Check {

    int balance = 0;
    int buffer = 0;

    int lastAirTick = 0;

    @Override
    public void handle(MovementData data)
    {

        if ( isBypass() ) return;
        if ( data.sinceStandingOnBoatTick <= 2 ) return;
        if ( data.sinceVehicleTick <= 2 ) return;
        if ( data.sinceClimbTick < 15 ) return;
        if ( data.sinceWebTick <= 2 ) return;


        if ( lastAirTick == 0 ) {
            lastAirTick = data.serverAirTick;
            return;
        }

        if ( data.sinceWaterTick < 20 ) return;
        if ( data.clientAirTick < 20 || lastAirTick < 20 ) {
            lastAirTick = data.serverAirTick;
            return;
        }

        int diff = Math.abs(data.clientAirTick - lastAirTick);

        balance += diff;
        balance -= 1;

        if ( balance > 25 ) {
            if ( buffer++ > config.getIntegerOrDefault("flag_buffer", 4) ) {
                flag("balance=" + balance);
                buffer = 0;
            }
            balance = 0;
        }

        lastAirTick = data.serverAirTick;
    }

}
