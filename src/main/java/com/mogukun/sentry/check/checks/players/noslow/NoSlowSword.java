package com.mogukun.sentry.check.checks.players.noslow;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import com.mogukun.sentry.models.Counter;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@CheckInfo(
        name = "NoSlow (Sword)",
        description = "Blocking with Sword while sprinting",
        category = Category.PLAYER
)
public class NoSlowSword extends Check {

    Counter counter = new Counter();

    @Override
    public void handle(MovementData data) {
        if ( !player.isSprinting() ) return;

        if ( player.isBlocking() ) {
            if ( counter.count() > 5 ) {
                flag();
            }
        }
    }
}
