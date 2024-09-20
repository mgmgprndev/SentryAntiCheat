package com.mogukun.sentry.check.checks.players.nuker;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;

@CheckInfo(
        name = "Nuker (A)",
        path = "player.nuker.a",
        description = "Nuker Check",
        category = Category.PLAYER
)
public class NukerA extends Check {


    @Override
    public void handle(Packet data) {
        // my approach is get block break events and count how mnay
        // like saving delta of breaking blocks, and avg, and size etc
        // if its suspicious, gonna flag
    }

}
