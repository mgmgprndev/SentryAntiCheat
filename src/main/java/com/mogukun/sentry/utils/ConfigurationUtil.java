package com.mogukun.sentry.utils;

import com.mogukun.sentry.models.ConfigurationData;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationUtil {


    public ConcurrentHashMap<String, ConfigurationData> datas = new ConcurrentHashMap<>();

    public ConfigurationUtil() {}

    public ConfigurationData getData(String path) {
        if ( datas.get(path) == null ) {
            ConfigurationData data = new ConfigurationData(path);
            datas.put(path, data);
            return data;
        }
        return datas.get(path);
    }

}
