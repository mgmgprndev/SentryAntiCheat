package com.mogukun.sentry.check.checks.players.noslow;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;
import com.mogukun.sentry.models.Counter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@CheckInfo(
        name = "NoSlow (Food)",
        path = "player.noslow.food",
        description = "Eating something while sprinting",
        category = Category.PLAYER
)
public class NoSlowFood extends Check {

    boolean isEating = false;
    long startEating = 0;
    Counter counter = new Counter();

    @Override
    public void handle(MovementData data) {
        if ( !player.isSprinting() ) return;

        if ( System.currentTimeMillis() - startEating > 600 ) {
            isEating = false;
        }

        if ( isEating ) {
            if ( counter.count() > config.getIntegerOrDefault("flag_buffer", 5) ) {
                flag();
            }
        }
    }

    @Override
    public void handle(Packet packet) {
        if ( packet instanceof PacketPlayInBlockDig ) {
            PacketPlayInBlockDig dig = (PacketPlayInBlockDig) packet;

            PacketPlayInBlockDig.EnumPlayerDigType digType = dig.c();

            if ( digType == PacketPlayInBlockDig.EnumPlayerDigType.DROP_ALL_ITEMS
                    || digType == PacketPlayInBlockDig.EnumPlayerDigType.DROP_ITEM
                    || digType == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM ) {
                isEating = false;
            }

        }
    }

    @Override
    public void event(Event event) {
        if ( event instanceof PlayerInteractEvent ) {
            PlayerInteractEvent ie = (PlayerInteractEvent) event;
            if ( ie.getAction() == Action.RIGHT_CLICK_AIR || ie.getAction() == Action.RIGHT_CLICK_BLOCK ) {
                ItemStack s = player.getItemInHand();
                if ( s != null && s.getType().isEdible() ) {
                    if ( player.getFoodLevel() != 20
                    || s.getType() == Material.GOLDEN_APPLE ) {
                        isEating = true;
                        startEating = System.currentTimeMillis();
                    }
                }
            }
        } else if ( event instanceof FoodLevelChangeEvent ) {
            isEating = false;
        }
    }
}
