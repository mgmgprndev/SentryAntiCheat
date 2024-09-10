package com.mogukun.sentry.check.checks.players.inventory;

import com.mogukun.sentry.check.*;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInfo(
        name = "Inventory (A)",
        path = "player.inventory.a",
        description = "Moving While Inventory Actions",
        category = Category.PLAYER
)
public class InventoryA extends Check {


    int buffer = 0;
    int sinceClick = 0;
    int ignore = 9999;

    boolean failed = false;

    @Override
    public void handle(MovementData data) {
        if ( !data.moving ) return;
        if ( ignore++ < 5 ) {
            return;
        }
        if ( sinceClick++ < 1 ) {

            if ( failed ) {

                if ( data.moving ) {
                    flag();
                }

                failed = false;
            }

            if ( buffer++ > 0 ) {
                failed = true;
                buffer = 0;
            }

        } else {
            buffer -= buffer > 0 ? 1 : 0;
        }
    }

    @Override
    public void event(Event event) {
        if ( event instanceof PlayerTeleportEvent ) {
            ignore = 0;
        }
        if ( event instanceof InventoryClickEvent ) {
            sinceClick = 0;
        }
    }
}
