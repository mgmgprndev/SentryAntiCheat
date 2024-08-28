package com.mogukun.sentry;

import com.mogukun.sentry.check.CheckManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sentry extends JavaPlugin {

    public CheckManager checkManager;
    public static Sentry instance;

    @Override
    public void onEnable() {
        this.instance = this;
        // define check manager
        checkManager = new CheckManager();
    }

    @Override
    public void onDisable() {
    }
}
