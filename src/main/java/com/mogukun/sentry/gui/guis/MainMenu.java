package com.mogukun.sentry.gui.guis;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.gui.GUI;
import com.mogukun.sentry.util.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainMenu extends GUI {

    public MainMenu() {
        super("Sentry Anti-Cheat");
    }

    @Override
    public Inventory createGUI(Player player) {
        Inventory inv = Bukkit.createInventory(player,3*9, title);

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, createStack(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.WHITE.getData()), "&1", null));
            }
        }

        inv.setItem(11, createStack(Material.BOOK, "&6Checks", new ListUtil().
                add("&1").
                add("&6Total " + Sentry.instance.checkManager.checks.size() + " checks" ).
                add("&6and " + Sentry.instance.checkUtil.enabledCheckSize() + " are enable" )
                .getList()));

        inv.setItem(13, createStack(Material.PAPER, "&6Sentry Information", new ListUtil().
                add("&1").
                add("&6Type &fFree").
                add("&6Version &f" + Sentry.instance.getDescription().getVersion())
                .getList()));


        inv.setItem(15, createStack(Material.REDSTONE, "&6Reload Config", new ListUtil().
                add("&1").
                add("&6You can reload config by clicking").
                add("&6You can also use /sentry reload instead")
                .getList()));

        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String clickedItem = event.getCurrentItem().getItemMeta().getDisplayName();
        if ( clickedItem == null ) clickedItem = "";

        if ( clickedItem.contains("Checks") ) {
            player.openInventory( new Check().setState("top").createGUI(player) );
        }

        if ( clickedItem.contains("Reload Config") ) {
            Bukkit.getServer().dispatchCommand(player,"sentry reload");
        }
    }

}
