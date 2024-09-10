package com.mogukun.sentry.models;

public class CheckStatus {

    public boolean enabled = true;
    public String path = "";

    public CheckStatus(String p, boolean b) {
        this.path = p;
        this.enabled = b;
    }

}
