package com.mogukun.sentry.check.checks.players.pingspoof;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;
import com.mogukun.sentry.models.DuoLong;

import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "PingSpoof (A)",
        path = "player.pingspoof.a",
        description = "Ping Spoof Detection",
        category = Category.PLAYER
)
public class PingSpoofA extends Check {

    ConcurrentLinkedDeque<DuoLong> keepAlive = new ConcurrentLinkedDeque<>();
    ConcurrentLinkedDeque<DuoLong> transaction = new ConcurrentLinkedDeque<>();

    @Override
    public void handle(MovementData data) {
        long now = System.currentTimeMillis();

        keepAlive.add(new DuoLong(getPing(), getPlayerData().lastInKeepAlive));
        transaction.add(new DuoLong(getTransactionPing(), getPlayerData().transactionReceived));

        keepAlive.removeIf(d -> now - d.b > 2000 );
        transaction.removeIf(d -> now - d.b > 2000 );

        Long closestKeepalive = null;
        Long closestTransaction = null;
        long minDifference = Long.MAX_VALUE;

        for (DuoLong keepAliveD : keepAlive) {
            for (DuoLong transactionD : transaction) {
                long difference = Math.abs(keepAliveD.b - transactionD.b);
                if (difference < minDifference) {
                    minDifference = difference;
                    closestKeepalive = keepAliveD.a;
                    closestTransaction = transactionD.a;
                }
            }
        }

        if ( closestKeepalive == null || closestTransaction == null ) return;

        long difference = Math.abs( closestKeepalive - closestTransaction );
        if ( difference > 500 ) {
            flag("difference=" + difference );
        }

    }

}
