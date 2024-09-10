package com.mogukun.sentry.check.checks.players.badpackets;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import net.minecraft.server.v1_8_R3.*;

@CheckInfo(
        name = "BadPacket (F)",
        path = "player.badpacket.f",
        description = "Ping Spoof",
        category = Category.PLAYER
)
public class BadPacketF extends Check {


    @Override
    public void handle(Packet thePacket) {
        if ( thePacket instanceof PacketPlayInKeepAlive ) {
            long transactionPing = getTransactionPing();
            long keepAlivePing = getPing();

            if ( transactionPing == Long.MAX_VALUE || keepAlivePing == Long.MAX_VALUE ) return;

            long diff = transactionPing - keepAlivePing;


        }
    }

}
