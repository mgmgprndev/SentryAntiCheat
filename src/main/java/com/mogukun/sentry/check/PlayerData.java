package com.mogukun.sentry.check;

public class PlayerData {
    public PlayerData() {}

    public MovementData data = null;

    boolean runningTransactionPingCheck = false;
    long transactionSent = 0;
    public long transactionReceived = 0;
    public long transactionPing = Long.MAX_VALUE;

    public long lastOutKeepAlive;
    public long lastInKeepAlive;
    public long ping = Long.MAX_VALUE;

    public int sinceFlying = 0;

    public boolean backOnGroundSinceFly = false;

    public int teleportTick = 0, sinceVelocityTakenTick = 0, respawnTick = 0;

}
