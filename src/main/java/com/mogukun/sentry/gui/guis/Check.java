package com.mogukun.sentry.gui.guis;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.gui.GUI;
import com.mogukun.sentry.util.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Check extends GUI {

    public Check() {
        super("Sentry Anti-Cheat Checks");
    }

    @Override
    public Inventory createGUI(Player player) {
        Inventory inv = Bukkit.createInventory(player,( state.equalsIgnoreCase("top") ? 3 : 6 ) *9, title);

        if ( state.equalsIgnoreCase("top") ) {
            for (int i = 0; i < 27; i++) {
                if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                    inv.setItem(i, createStack(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.WHITE.getData()), "&1", null));
                }
            }

            inv.setItem(11, createStack(Material.DIAMOND_SWORD, "&6Combat", null ));
            inv.setItem(13, createStack(Material.FEATHER, "&6Movement", null ));
            inv.setItem(15, createStack(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()), "&6Player", null ));
        } else {
            for (int i = 0; i < 9; i++) {
                inv.setItem(i, createStack(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.WHITE.getData()),
                        "&6Previous Page", null));
            }

            if ( state.equalsIgnoreCase("combat") || state.equalsIgnoreCase("movement") || state.equalsIgnoreCase("player") ) {

                int i = 9;

                for ( com.mogukun.sentry.check.Check c : Sentry.instance.checkManager.checks ) {
                    if ( c.checkInfo.path().startsWith( state +  "." ) ) {
                        boolean isEnabled = Sentry.instance.checkUtil.isEnabled(c.checkInfo.path());

                        inv.setItem(i++, createStack( isEnabled ? Material.ENCHANTED_BOOK : Material.BOOK , "&6" + c.checkInfo.name() ,
                                new ListUtil().add(isEnabled ? "&6Click To &cDisable" : "&6Click To &aEnable").getList()));
                    }
                }

            }
        }

        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String clickedItem = event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase();
        if ( clickedItem == null ) clickedItem = "";

        for ( com.mogukun.sentry.check.Check c : Sentry.instance.checkManager.checks ) {
            if ( clickedItem.contains(c.checkInfo.name().toLowerCase()) ) {
                Sentry.instance.checkUtil.setStatus(c.checkInfo.path(),!Sentry.instance.checkUtil.isEnabled(c.checkInfo.path()));
                String cate = c.checkInfo.path().split("\\.")[0];
                player.openInventory( new Check().setState(cate).createGUI(player) );
                return;
            }
        }

        if ( clickedItem.contains("previous page") ) player.openInventory( new Check().setState("top").createGUI(player) );
        if ( clickedItem.contains("combat") ) player.openInventory( new Check().setState("combat").createGUI(player) );
        if ( clickedItem.contains("movement") ) player.openInventory( new Check().setState("movement").createGUI(player) );
        if ( clickedItem.contains("player") ) player.openInventory( new Check().setState("player").createGUI(player) );

    }

}
