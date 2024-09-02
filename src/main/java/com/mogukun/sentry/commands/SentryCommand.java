package com.mogukun.sentry.commands;

import com.mogukun.sentry.Sentry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SentryCommand implements CommandExecutor {

    String prefix = "&8&l[&6&lSENTRY&8&l]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if ( sender instanceof Player )
        {
            Player player = ((Player) sender).getPlayer();


            if ( !player.hasPermission("sentry.command") ) {
                player.sendMessage(chatColor("&c You are not allowed to run this command!"));
                return false;
            }

            if ( args.length == 0 )
            {

                String temp = "&6&l&m==================================================\n"
                        + "\n"
                        + "&6&l SentryAntiCheat " + Sentry.instance.getDescription().getVersion() + "\n"
                        + "\n"
                        + "&6/sentry - show information\n"
                        + "&6/sentry alerts - enable alert\n"
                        + "\n"
                        + "&6&l&m==================================================";

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', temp));

            }
            else if ( args.length == 1 )
            {
                if ( args[0].equalsIgnoreCase("alerts") )
                {
                    UUID uuid = player.getUniqueId();
                    Sentry.instance.alertStatus.putIfAbsent(uuid,0);

                    if ( Sentry.instance.alertStatus.get(uuid) != 0 ) {
                        Sentry.instance.alertStatus.put(uuid, 0);
                        player.sendMessage(chatColor("&a Alert is &cDisabled"));
                    } else {
                        Sentry.instance.alertStatus.put(uuid, 1);
                        player.sendMessage(chatColor("&a Alert is Enabled"));
                    }

                }
            }




        }


        return false;
    }


    private String chatColor(String s){
        return ChatColor.translateAlternateColorCodes('&',prefix+s);
    }
}
