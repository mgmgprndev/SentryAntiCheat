package com.mogukun.sentry.models;

import org.bukkit.util.Vector;

public class PlayerData {
    public PlayerData() {}

    public MovementData data = null;
    public boolean runningTransactionPingCheck = false;
    public long transactionSent = 0;
    public long transactionReceived = 0;
    public long transactionPing = Long.MAX_VALUE;

    public Vector velocityTaken;

    public long lastOutKeepAlive;
    public long lastInKeepAlive;
    public long ping = Long.MAX_VALUE;

    public int sinceFlying = 0;

    public boolean backOnGroundSinceFly = false;

    public int teleportTick = 0, sinceVelocityTakenTick = 0, respawnTick = 0;

}
