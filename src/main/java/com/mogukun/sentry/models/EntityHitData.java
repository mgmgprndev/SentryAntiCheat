package com.mogukun.sentry.models;

import java.util.UUID;

public class EntityHitData {

    public UUID uuid;
    public long timestamp;

    public EntityHitData(UUID uuid) {
        this.uuid = uuid;
        this.timestamp = System.currentTimeMillis();
    }

}
