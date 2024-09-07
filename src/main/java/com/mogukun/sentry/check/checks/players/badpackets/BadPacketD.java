package com.mogukun.sentry.check.checks.players.badpackets;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

@CheckInfo(
        name = "BadPacket (D)",
        description = "Invalid Entity Attack",
        category = Category.PLAYER
)
public class BadPacketD extends Check {

    @Override
    public void handle(Packet packet) {
        if ( packet instanceof PacketPlayInUseEntity ) {
            PacketPlayInUseEntity packetUseEntity = (PacketPlayInUseEntity) packet;
            CraftPlayer p = (CraftPlayer) player;
            if (packetUseEntity.a(p.getHandle().getWorld()) == null) flag();
        }
    }

}
