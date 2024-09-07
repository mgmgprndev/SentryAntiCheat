package com.mogukun.sentry.check;

public class PlayerData {

    public PlayerData() {}

    public MovementData data = null;

    public long lastOutKeepAlive;
    public long lastInKeepAlive;
    public long ping = Long.MAX_VALUE;

    public int teleportTick = 0, sinceVelocityTakenTick = 0, respawnTick = 0;

}
