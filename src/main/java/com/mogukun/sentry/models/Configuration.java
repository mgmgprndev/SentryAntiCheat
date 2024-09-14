package com.mogukun.sentry.models;

import com.mogukun.sentry.Sentry;
public class Configuration {

    String path;

    public Configuration(String path) {
        this.path = path;
    }

    public Double getDoubleOrDefault(String st, double def) {
        return (Double) Sentry.instance.configurationUtil.getData(path).getObject(st, def);
    }

    public Integer getIntegerOrDefault(String st, int def) {
        return (Integer) Sentry.instance.configurationUtil.getData(path).getObject(st, def);
    }
}
