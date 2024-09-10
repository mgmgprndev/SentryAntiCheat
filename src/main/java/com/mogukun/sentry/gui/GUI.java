package com.mogukun.sentry.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUI {

    public String title;
    public String state = "";

    public GUI setState(String s) {
        this.state = s;
        return this;
    }

    public GUI(String title) {
        this.title = title;
    }

    public Inventory createGUI(Player player) {
        return null;
    }


    public ItemStack createStack(ItemStack s, String name, List<String> lore) {
        ItemMeta meta = s.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if ( lore != null ) {
            List<String> colorLore = new ArrayList<>();
            for ( String st : lore ) colorLore.add(ChatColor.translateAlternateColorCodes('&', st));
            meta.setLore(colorLore);
        }
        s.setItemMeta(meta);
        return s;
    }

    public ItemStack createStack(Material mate, String name, List<String> lore) {
        return createStack(new ItemStack(mate), name, lore);
    }

    public void handleClick(InventoryClickEvent event) {}

}
