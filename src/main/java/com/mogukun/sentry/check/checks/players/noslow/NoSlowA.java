package com.mogukun.sentry.check.checks.players.noslow;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

@CheckInfo(
        name = "NoSlow (A)",
        description = "Eating something without slowing down",
        category = Category.PLAYER
)
public class NoSlowA extends Check {


    int sinceEating = 99999;

    @Override
    public void handle(MovementData data) {
        if ( !player.isSprinting() ) return;

        //if ( sinceEating++ < 2 ) debug("? " + sinceEating );
    }


    @Override
    public void event(Event event) {
        if ( event instanceof PlayerInteractEvent ) {
            PlayerInteractEvent ie = (PlayerInteractEvent) event;
            if ( ie.getAction() == Action.RIGHT_CLICK_AIR || ie.getAction() == Action.RIGHT_CLICK_BLOCK ) {
                ItemStack s = player.getItemInHand();
                if ( s != null && s.getType().isEdible() ) {
                    if ( player.getFoodLevel() != 20 ) {
                        sinceEating = 0;
                    }
                }
            }
        }
    }
}
