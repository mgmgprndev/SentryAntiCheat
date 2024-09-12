package com.mogukun.sentry.check.checks.players.badpackets;

import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.MovementData;
import com.mogukun.sentry.utils.MathUtil;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInfo(
        name = "BadPacket (C)",
        path = "player.badpacket.c",
        description = "Invalid Flying Y Position Packet",
        category = Category.PLAYER
)
public class BadPacketC extends Check {


    int teleport = 99999;

    // Credit @DerRedstoner, from CheatGuard.

    @Override
    public void handle(MovementData data) {
        if ( teleport++ < 3 ) {
            return;
        }

        double gcd = MathUtil.gcd(data.currentY,data.lastY);
        if(String.valueOf(gcd).contains("E")) flag("gcd=" + gcd);

    }

    @Override
    public void event(Event event) {
        if ( event instanceof PlayerTeleportEvent ) {
            teleport = 0;
        }
    }
}
