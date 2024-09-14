package com.mogukun.sentry.commands;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.models.PlayerData;
import com.mogukun.sentry.utils.ConfigurationUtil;
import com.mogukun.sentry.utils.PlayerDataUtil;
import com.mogukun.sentry.gui.guis.MainMenu;
import com.mogukun.sentry.utils.CheckUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.UUID;

public class SentryCommand implements CommandExecutor {

    String prefix = "&8&l[&6&lSENTRY&8&l]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if ( sender instanceof Player )
        {
            Player player = ((Player) sender).getPlayer();


            if ( !player.hasPermission("sentry.admin") ) {
                player.sendMessage(chatColor("&c You are not allowed to run this command!"));
                return false;
            }

            if ( args.length == 0 )
            {

                String temp = "&6&m----------------------------------------------------&r\n"
                        + " \n"
                        + "&6&l SentryAntiCheat " + Sentry.instance.getDescription().getVersion() + "\n"
                        + " \n"
                        + "&6 /sentry - show information\n"
                        + "&6 /sentry reload - reload config\n"
                        + "&6 /sentry alerts - enable alert\n"
                        + "&6 /sentry gui - Sentry GUI Interface\n"
                        + "&6 /sentry tps - Show Server TPS\n"
                        + "&6 /sentry ping <player> - get ping of player\n"
                        + "&6 /sentry info <player> - get info from ip-api.com\n"
                        + " \n"
                        + "&6 Only For Testing: \n"
                        + "&6 /sentry debug - show alerts of you only to you\n"
                        + "&6 /sentry test-server - test server mode is everyone see own alerts\n"
                        + " \n"
                        + "&6&m----------------------------------------------------";

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',temp));

            }
            else if ( args.length == 1 )
            {
                if ( args[0].toLowerCase().startsWith("alert") )
                {
                    UUID uuid = player.getUniqueId();
                    Sentry.instance.alertStatus.putIfAbsent(uuid,0);

                    if ( Sentry.instance.alertStatus.get(uuid) == 1 ) {
                        Sentry.instance.alertStatus.put(uuid, 0);
                        player.sendMessage(chatColor("&a Alert is &cDisabled"));
                    } else if ( Sentry.instance.alertStatus.get(uuid) == 2 ) {
                        Sentry.instance.alertStatus.put(uuid, 1);
                        player.sendMessage(chatColor("&a Debug is &cDisabled&a and Alert is Enabled"));
                    } else {
                        Sentry.instance.alertStatus.put(uuid, 1);
                        player.sendMessage(chatColor("&a Alert is Enabled"));
                    }

                }
                else if ( args[0].equalsIgnoreCase("info") ) {
                    player.sendMessage(chatColor("&c/sentry info <player>"));
                }
                else if ( args[0].equalsIgnoreCase("ping") ) {
                    player.sendMessage(chatColor("&c/sentry ping <player>"));
                }
                else if ( args[0].toLowerCase().startsWith("debug") )
                {
                    UUID uuid = player.getUniqueId();
                    Sentry.instance.alertStatus.putIfAbsent(uuid,0);

                    if ( Sentry.instance.alertStatus.get(uuid) == 2 ) {
                        Sentry.instance.alertStatus.put(uuid, 0);
                        player.sendMessage(chatColor("&a Debug is &cDisabled"));
                    } else if ( Sentry.instance.alertStatus.get(uuid) == 1 ) {
                        Sentry.instance.alertStatus.put(uuid, 2);
                        player.sendMessage(chatColor("&a Alert is &cDisabled&a and Debug is Enabled"));
                    } else {
                        Sentry.instance.alertStatus.put(uuid, 2);
                        player.sendMessage(chatColor("&a Debug is Enabled"));
                    }
                }else if ( args[0].toLowerCase().startsWith("test-server") )
                {
                    if ( Sentry.instance.testServer ) {
                        Sentry.instance.testServer = false;
                        player.sendMessage(chatColor("&a Test-Server is &cDisabled"));
                    } else {
                        Sentry.instance.testServer = true;
                        player.sendMessage(chatColor("&a Test-Server is Enabled"));
                    }
                }
                else if ( args[0].toLowerCase().startsWith("reload") ) {
                    player.sendMessage(chatColor("&a Reloading Config..."));
                    Sentry.instance.reloadConfig();
                    Sentry.instance.config = Sentry.instance.getConfig();
                    Sentry.instance.checkUtil = new CheckUtil();
                    Sentry.instance.configurationUtil = new ConfigurationUtil();
                    player.sendMessage(chatColor("&a Reloaded Config!"));
                }
                else if ( args[0].toLowerCase().startsWith("gui") ) {
                    player.openInventory( new MainMenu().createGUI(player) );
                }
                else if ( args[0].toLowerCase().startsWith("tps") ) {
                    double tps = Sentry.instance.tps.tps;
                    String color = "";
                    if ( tps < 19.4 ) {
                        color = "&e";
                    }
                    if ( tps < 15 ) {
                        color = "&c";
                    }
                    player.sendMessage(chatColor("&a TPS: " + color + tps ));
                }
            }
            else if ( args.length == 2 )
            {
                if ( args[0].equalsIgnoreCase("ping") ) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if ( target == null ) {
                        player.sendMessage(chatColor("&aOffline Player cannot lookup!"));
                        return false;
                    }

                    PlayerData data = Sentry.instance.dataManager.getPlayerData(target);
                    new PlayerDataUtil(target).runTransactionPingCheck();
                    if ( data == null ) {
                        player.sendMessage(chatColor("&cSorry, couldn't get the ping."));
                        return false;
                    }
                    long diffK = System.currentTimeMillis() - data.lastInKeepAlive;
                    long diffT = System.currentTimeMillis() - data.transactionReceived;

                    // player.sendMessage(chatColor("&a" + target.getName() + "'s ping is " + data.ping + "ms. (last checked: " + diff + "ms ago)"  ));

                    String temp = "&6&m----------------------------------------------------&r\n"
                            + " \n"
                            + "&6&l PING OF &r&6" + target.getName() + "&l!\n"
                            + " \n"
                            + "&6 PING METHOD:\n"
                            + "&6 | KEEP ALIVE: " + ( data.ping != Long.MAX_VALUE ? data.ping + "ms. (last checked: " + diffK + "ms ago)" : "&cNever Checked, retry later!&r" ) + "\n"
                            + "&6 | TRANSACTION: " + ( data.transactionPing != Long.MAX_VALUE ? data.transactionPing + "ms. (last checked: " + diffT + "ms ago)" : "&cNever Checked, retry later!&r" ) + "\n"
                            + " \n"
                            + "&6* We have two ping methods for when player spoofing ping, If they are legit, mostly both will be really near values.\n"
                            + " \n"
                            + "&6&m----------------------------------------------------";
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',temp));

                } else if ( args[0].equalsIgnoreCase("info") ) {


                    String target = args[1];
                    Player targetPlayer = Bukkit.getPlayer(args[1]);
                    String name = "", ip = "";

                    if ( isValidIP(target) ) {
                        name = target;
                        ip = target;
                    } else if ( targetPlayer != null ) {
                        name = targetPlayer.getName();
                        ip = targetPlayer.getAddress().getAddress().getHostAddress();
                    } else {
                        player.sendMessage(chatColor("&a'" + target + "' is not valid IP or Online Player Name."));
                        return false;
                    }

                    player.sendMessage(chatColor("&cLooking up..."));
                    runCheck(ip, name, player);

                }
            }
        } else {
            sender.sendMessage("§cThis command only executable from in-game.");
        }


        return false;
    }

    private void runCheck(String ip, String name, Player player) {
        new Thread(() -> {
            String apiUrl = "http://ip-api.com/json/" + ip;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                String status = jsonResponse.optString("status");

                if ( !status.equalsIgnoreCase("success") ) {
                    player.sendMessage(chatColor("&cError, Message: " + jsonResponse.optString("message") ));
                    return;
                }

                String continent = jsonResponse.optString("continent", "Unknown");
                String country = jsonResponse.optString("country", "Unknown");
                String region = jsonResponse.optString("regionName", "Unknown");
                String city = jsonResponse.optString("city", "Unknown");
                String zip = jsonResponse.optString("zip", "Unknown");
                String isp = jsonResponse.optString("isp", "Unknown");
                String asn = jsonResponse.optString("as", "Unknown");

                String temp = "&6&m----------------------------------------------------&r\n"
                        + " \n"
                        + "&6&l LOOK UP RESULT OF &r&6" + name + "&l!\n"
                        + " \n"
                        + ( name == ip ? "&6 IP: " + ip + "\n \n" : "" )
                        + "&6 Address: \n"
                        + "&6 | Continent: " + continent + "\n"
                        + "&6 | Country: " + country + "\n"
                        + "&6 | Region: " + region + "\n"
                        + "&6 | City: " + city + "\n"
                        + "&6 | ZIP: " + zip + "\n"
                        + " \n"
                        + "&6 ISP: \n"
                        + "&6 | ISP: " + isp + "\n"
                        + "&6 | ASN: " + asn + "\n"
                        + " \n"
                        + "&6&m----------------------------------------------------";

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',temp));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean isValidIP(String ip) {
        try {
            InetAddress ignored = InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private String chatColor(String s){
        return ChatColor.translateAlternateColorCodes('&',prefix+"&r "+s);
    }
}
