package com.mogukun.sentry.check;

import java.util.UUID;

public class ViolationData {
    
    public final long timeStamp = System.currentTimeMillis();
    public UUID uuid;
    public String check;
    
    public ViolationData(UUID uuid, String check) {
        this.uuid = uuid;
        this.check = check;
    }
    
}
