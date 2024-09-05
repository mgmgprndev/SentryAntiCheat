package com.mogukun.sentry.models;

import org.bukkit.Location;

import java.util.UUID;

public class LocationTimeStamp {

    public UUID uuid;
    public long timestamp;
    public Location loc;

    public LocationTimeStamp(UUID uuid, Location l) {
        this.uuid = uuid;
        this.loc = l;
        this.timestamp = System.currentTimeMillis();
    }

}
