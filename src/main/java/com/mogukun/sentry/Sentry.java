package com.mogukun.sentry;

import com.mogukun.sentry.check.CheckManager;
import com.mogukun.sentry.check.PlayerDataManager;
import com.mogukun.sentry.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sentry extends JavaPlugin {

    public CheckManager checkManager;
    public PlayerDataManager dataManager;
    public static Sentry instance;

    @Override
    public void onEnable() {

        long startLoading = System.currentTimeMillis();

        System.out.println("[Sentry] Loading Sentry AntiCheat");

        this.instance = this;
        dataManager = new PlayerDataManager();
        checkManager = new CheckManager();
        getServer().getPluginManager().registerEvents( new PlayerListener(), this );

        System.out.println("[Sentry] Loaded Sentry AntiCheat in " + ( System.currentTimeMillis() - startLoading ) + "ms.");
    }

    @Override
    public void onDisable() {
    }
}
