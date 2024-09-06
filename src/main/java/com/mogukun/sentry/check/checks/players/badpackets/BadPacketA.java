package com.mogukun.sentry.check.checks.players.badpackets;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInfo(
        name = "BadPacket (A)",
        description = "Invalid Flying Packet Check",
        category = Category.PLAYER
)
public class BadPacketA extends Check {

    int s = 0;

   // untested, my friend let me create this :thinking:

    @Override
    public void handle(Packet packet) {
        if ( packet instanceof PacketPlayInFlying && player.getVehicle() == null ) {
            PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            if ( flying.g() ) {
                s = 0;
            } else {
                if ( s++ > 20 ) flag("s="+s);
            }
        }

    }

}
