package com.mogukun.sentry.gui;

import com.mogukun.sentry.gui.guis.Check;
import com.mogukun.sentry.gui.guis.MainMenu;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class GUIManager {

    ArrayList<GUI> guis = new ArrayList<>();

    public GUIManager() {
        guis.add( new MainMenu() );
        guis.add( new Check() );
    }


    public void callEvent(InventoryClickEvent event) {
        if ( event.getInventory() == null ) return;
        if ( event.getCurrentItem() == null ) return;
        if ( event.getCurrentItem().getItemMeta() == null ) return;
        if ( !event.getWhoClicked().hasPermission("sentry.admin") ) return;

        String title = event.getView().getTitle();
        for ( GUI g : guis ) {
            if ( g.title == title ) {
                event.setCancelled(true);
                if ( event.getClickedInventory() != event.getInventory() ) {
                    return;
                }
                g.handleClick(event);
                return;
            }
        }
    }


}
