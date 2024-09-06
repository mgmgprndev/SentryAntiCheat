package com.mogukun.sentry.commands;

import com.mogukun.sentry.Sentry;
import org.bukkit.Bukkit;
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

                String temp = "&6&m----------------------------------------------------\n\n"
                        + "\n\n"
                        + "&6&l SentryAntiCheat " + Sentry.instance.getDescription().getVersion() + "\n"
                        + "\n\n"
                        + "&6/sentry - show information\n"
                        + "&6/sentry alerts - enable alert\n"
                        + "&6/sentry info <player> - get many information from database. \n"
                        + "\n\n"
                        + "&6&m----------------------------------------------------";

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', temp.replace("\n","<BR>").replace("<BR>","\n")));

            }
            else if ( args.length == 1 )
            {
                if ( args[0].toLowerCase().startsWith("alert") )
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
                else if ( args[0].equalsIgnoreCase("info") ) {
                    player.sendMessage(chatColor("&c/sentry info <player>"));
                }
            }
            else if ( args.length == 2 )
            {
                Player target = Bukkit.getPlayer(args[1]);
                if ( target == null ) {
                    player.sendMessage(chatColor("&aOffline Player cannot lookup!"));
                }
                else
                {

                    // something do here

                }
            }
        } else {
            sender.sendMessage("§cThis command only executable from in-game.");
        }


        return false;
    }


    private String chatColor(String s){
        return ChatColor.translateAlternateColorCodes('&',prefix+s);
    }
}
