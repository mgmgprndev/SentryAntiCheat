package com.mogukun.sentry.check.checks.players.groundspoof;

import com.mogukun.sentry.check.*;

@CheckInfo(
        name = "GroundSpoof (A)",
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

        if ( data.sinceClimbTick < 15 ) return;

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
            if ( buffer++ > 4 ) {
                flag("balance=" + balance);
                buffer = 0;
            }
            balance = 0;
        }

        lastAirTick = data.serverAirTick;
    }

}
