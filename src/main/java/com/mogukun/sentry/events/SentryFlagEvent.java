package com.mogukun.sentry.events;

import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.ViolationData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SentryFlagEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();


    Player player;
    String checkName, checkPath, checkDescription, category, debug;
    boolean isExperimental;
    int totalVL, perCheckVL;

    public SentryFlagEvent(CheckInfo info, int totalVL, int perCheckVL, Player player, String debug ) {
        this.player = player;
        this.debug = debug;
        this.totalVL = totalVL;
        this.perCheckVL = perCheckVL;
        checkName = info.name();
        checkPath = info.path();
        checkDescription = info.description();
        isExperimental = info.experimental();
        category = info.category().toString();
    }

    public Player getPlayer() {
        return player;
    }

    public String getDebug() {
        return debug;
    }

    public int getTotalVL() {
        return totalVL;
    }

    public int getPerCheckVL() {
        return perCheckVL;
    }

    public String getCheckName() {
        return checkName;
    }

    public String getCheckPath() {
        return checkPath;
    }

    public String getCheckDescription() {
        return checkDescription;
    }

    public String getCheckCategory() {
        return category;
    }

    public boolean isExperimental() {
        return isExperimental;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }


}
