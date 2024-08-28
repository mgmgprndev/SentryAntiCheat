package com.mogukun.sentry.check;

import net.minecraft.server.v1_8_R3.Packet;

public class Check {

    public CheckInfo checkInfo;

    public Check(){
        checkInfo = this.getClass().getAnnotation(CheckInfo.class);
    }

    public CheckResult handle(Packet packet){
        return null;
    }

}
