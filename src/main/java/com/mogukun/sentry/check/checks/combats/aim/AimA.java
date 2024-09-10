package com.mogukun.sentry.check.checks.combats.aim;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.EntityHitData;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.UUID;

@CheckInfo(
        name = "Aim (A)",
        description = "",
        category = Category.COMBAT
)
public class AimA extends Check {

    @Override
    public void event(Event event){
        if ( event instanceof EntityDamageByEntityEvent ) {// still no idea
        }
    }

}
