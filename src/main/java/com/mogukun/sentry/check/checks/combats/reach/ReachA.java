package com.mogukun.sentry.check.checks.combats.reach;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import com.mogukun.sentry.models.LocationTimeStamp;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "Reach (A)",
        description = "A Shit Reach Check",
        category = Category.COMBAT
)
public class ReachA extends Check {

    ConcurrentLinkedDeque<LocationTimeStamp> locationTimeStamps = new ConcurrentLinkedDeque<>();

    long ping = 0;

    @Override
    public void handle(MovementData data)
    {
        Location playerLocation = player.getLocation().clone();
        ping = data.ping;

        Iterator<Entity> iterator = player.getWorld().getEntities().iterator();
        while ( iterator.hasNext() ) {
            Entity ent = iterator.next();
            Location  entLocation = ent.getLocation().clone();
            double dist = entLocation.distance(playerLocation);
            if ( dist < 10 ) {
                locationTimeStamps.add(new LocationTimeStamp(ent.getUniqueId(), entLocation));
            }
        }

        locationTimeStamps.removeIf(s -> System.currentTimeMillis() - s.timestamp > 1000);
    }

    @Override
    public void event(Event event){
        if ( event instanceof EntityDamageByEntityEvent) {
            long now = System.currentTimeMillis();
            UUID uid = ((EntityDamageByEntityEvent) event).getEntity().getUniqueId();
            double minimumDistance = Double.MAX_VALUE;

            Location loc = player.getLocation().clone();

            for ( LocationTimeStamp lts : locationTimeStamps ) {
                if ( lts.uuid != uid  ) continue;

                long diff = now - ping - lts.timestamp;
                if ( diff < 250 ) {
                    double dist = loc.distance(lts.loc);
                    if ( minimumDistance > dist ) minimumDistance = dist;
                }
            }

            if ( minimumDistance == Double.MAX_VALUE ) return;

            if ( minimumDistance > 3.5 ) flag("distance=" + minimumDistance);
        }
    }

}
