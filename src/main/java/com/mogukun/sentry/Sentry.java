package com.mogukun.sentry;

import com.mogukun.sentry.check.CheckManager;
import com.mogukun.sentry.check.PlayerDataManager;
import com.mogukun.sentry.commands.SentryCommand;
import com.mogukun.sentry.gui.GUIManager;
import com.mogukun.sentry.listeners.PacketHandler;
import com.mogukun.sentry.listeners.PlayerListener;
import com.mogukun.sentry.models.ServerTPS;
import com.mogukun.sentry.util.CheckUtil;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;

public final class Sentry extends JavaPlugin {

    public CheckUtil checkUtil;
    public CheckManager checkManager;
    public PlayerDataManager dataManager;
    public static Sentry instance;
    public ServerTPS tps;

    public boolean testServer = false;

    public HashMap<UUID,Integer> alertStatus = new HashMap<>();

    public Configuration config;
    public GUIManager guiManager;

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
        getServer().getPluginManager().registerEvents( new PlayerListener(), this );
        getCommand("sentry").setExecutor( new SentryCommand() );
        for ( Player p : Bukkit.getOnlinePlayers() ) Sentry.instance.checkManager.checkMap.remove(p.getUniqueId());
        System.out.println("[Sentry] Loaded Sentry AntiCheat in " + ( System.currentTimeMillis() - startLoading ) + "ms.");
    }
}
