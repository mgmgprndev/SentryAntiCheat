package com.mogukun.sentry.models;

import com.mogukun.sentry.Sentry;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationData {

    public String path;
    public ConcurrentHashMap<String,Object> map;

    public ConfigurationData(String path) {
        this.path = path;
        this.map = new ConcurrentHashMap<>();
    }

    public Object getObject(String setting, Object defaultValue) {
        if ( map.get(setting) == null ) {
            Object fromData = Sentry.instance.checkUtil.getObject(path, setting);;
            if ( fromData == null ) {
                Sentry.instance.checkUtil.setObject(path, setting, defaultValue);
                map.put(setting, defaultValue);
            } else {
                map.put(setting, fromData);
            }
        }
        return map.get(setting);
    }

}
