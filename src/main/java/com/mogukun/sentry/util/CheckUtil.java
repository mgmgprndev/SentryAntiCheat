package com.mogukun.sentry.util;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.models.CheckStatus;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CheckUtil {

    ConcurrentLinkedDeque<CheckStatus> statuses = new ConcurrentLinkedDeque<>();

    public CheckUtil() {
        boolean hasChanged = false;
        for (Check check : Sentry.instance.checkManager.checks) {
            String originalPath = check.checkInfo.path();
            String path = "checks." + originalPath;
            if ( Sentry.instance.config.get(path) == null ) {
                Sentry.instance.config.set(path, true );
                statuses.add(new CheckStatus(originalPath,true));
                hasChanged = true;
            } else statuses.add(new CheckStatus(originalPath, Sentry.instance.config.getBoolean(path)));
        }
        if ( hasChanged ) {
            Sentry.instance.saveConfig();
            Sentry.instance.reloadConfig();
            Sentry.instance.config = Sentry.instance.getConfig();
        }
    }

    public boolean isEnabled(String path) {
        CheckStatus s = getCheckStatus(path);
        return s != null && s.enabled;
    }


    public int enabledCheckSize() {
        int i = 0;
        for ( CheckStatus s : statuses ) if ( s.enabled ) i++;
        return i;
    }

    public void setStatus(String path, boolean newStatus) {
        CheckStatus s = getCheckStatus(path);
        if ( s != null ) {
            s.enabled = newStatus;
            Sentry.instance.config.set("checks." + path, newStatus);
            Sentry.instance.saveConfig();
            Sentry.instance.reloadConfig();
            Sentry.instance.config = Sentry.instance.getConfig();
        }
    }

    public CheckStatus getCheckStatus(String path) {
        for ( CheckStatus s : statuses ) {
            if ( s.path == path ) return s;
        }
        return null;
    }

}
