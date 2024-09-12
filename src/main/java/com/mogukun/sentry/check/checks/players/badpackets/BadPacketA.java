package com.mogukun.sentry.check.checks.players.badpackets;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;

@CheckInfo(
        name = "BadPacket (A)",
        path = "player.badpacket.a",
        description = "Invalid Flying Packet Check",
        category = Category.PLAYER
)
public class BadPacketA extends Check {

    int s = 0;

    @Override
    public void handle(MovementData data) {
        if ( player.getVehicle() != null ) return;

        if ( data.moving ) {
            s = 0;
        } else {
            if ( s++ > 20 ) flag("s="+s);
        }
    }

}
