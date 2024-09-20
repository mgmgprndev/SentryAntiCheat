package com.mogukun.sentry;

import com.mogukun.sentry.check.CheckManager;
import com.mogukun.sentry.check.Plan;
import com.mogukun.sentry.listeners.MessageListener;
import com.mogukun.sentry.managers.PlayerDataManager;
import com.mogukun.sentry.commands.SentryCommand;
import com.mogukun.sentry.gui.GUIManager;
import com.mogukun.sentry.listeners.PlayerListener;
import com.mogukun.sentry.managers.ServerTPS;
import com.mogukun.sentry.utils.CheckUtil;
import com.mogukun.sentry.utils.ConfigurationUtil;
import com.mogukun.sentry.utils.PlayerPacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Sentry extends JavaPlugin {

    public CheckUtil checkUtil;
    public CheckManager checkManager;
    public PlayerDataManager dataManager;
    public static Sentry instance;
    public ServerTPS tps;

    public boolean testServer = false;

    public HashMap<UUID,Integer> alertStatus = new HashMap<>();

    public Configuration config;
    public Plan plan = Plan.Free;
    public GUIManager guiManager;
    public ConfigurationUtil configurationUtil;
    public PlayerPacketUtil playerPacketUtil;

    @Override
    public void onEnable() {

        long startLoading = System.currentTimeMillis();

        System.out.println("[Sentry] Loading Sentry AntiCheat");
        this.instance = this;
        saveDefaultConfig();
        config = getConfig();
        this.tps = new ServerTPS();
        dataManager = new PlayerDataManager();
        checkManager = new CheckManager();
        checkUtil = new CheckUtil();
        guiManager = new GUIManager();
        configurationUtil = new ConfigurationUtil();
        playerPacketUtil = new PlayerPacketUtil();
        getServer().getPluginManager().registerEvents( new PlayerListener(), this );
        getCommand("sentry").setExecutor( new SentryCommand() );
        Bukkit.getMessenger().registerIncomingPluginChannel(this,"mc:brand", new MessageListener());
        for ( Player player : Bukkit.getOnlinePlayers() ) playerPacketUtil.addPlayer(player);

        System.out.println("[Sentry] Loaded Sentry AntiCheat in " + ( System.currentTimeMillis() - startLoading ) + "ms.");
    }

    @Override
    public void onDisable() {
        Sentry.instance.checkManager.checkMap.clear();
        Sentry.instance.checkManager.vl.clear();
        Sentry.instance.playerPacketUtil.clear();
    }
}
