package com.mogukun.sentry.check.checks.combats.reach;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;
import com.mogukun.sentry.models.LocationTimeStamp;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
@CheckInfo(
        name = "Reach (A)",
        path = "combat.reach.a",
        description = "A Shit Reach Check",
        category = Category.COMBAT
)
public class ReachA extends Check {

    ConcurrentLinkedDeque<LocationTimeStamp> locationTimeStamps = new ConcurrentLinkedDeque<>();

    long ping = 0;

    Location loc;

    @Override
    public void handle(MovementData data)
    {
        loc = new Location(player.getWorld(), data.currentX, data.currentY, data.currentZ, data.currentYaw, data.currentPitch);
        ping = data.ping;

        locationTimeStamps.removeIf(s -> System.currentTimeMillis() - s.timestamp > 1000);

        List<Entity> entities;

        try {
            entities = new ArrayList<>(loc.getWorld().getEntities());
            for ( Entity ent : entities ) {
                Location  entLocation = ent.getLocation().clone();
                double dist = entLocation.distance(loc);
                if ( dist < 10 ) {
                    locationTimeStamps.add(new LocationTimeStamp(ent.getUniqueId(), entLocation));
                }
            }
        } catch (Exception ignore) {}

    }

    @Override
    public void event(Event event){
        if ( event instanceof EntityDamageByEntityEvent) {

            if ( loc == null ) return;

            long now = System.currentTimeMillis();
            UUID uid = ((EntityDamageByEntityEvent) event).getEntity().getUniqueId();
            double minimumDistance = Double.MAX_VALUE;

            for ( LocationTimeStamp lts : locationTimeStamps ) {
                if ( lts.uuid != uid  ) continue;

                long diff = now - ping - lts.timestamp;
                if ( diff < 500 ) {

                    double dist = loc.distance(lts.loc);

                    if ( minimumDistance > dist ) minimumDistance = dist;
                }
            }

            if ( minimumDistance == Double.MAX_VALUE ) return;

            if ( minimumDistance > config.getDoubleOrDefault("max_distance", 3.5) ) flag("distance=" + minimumDistance);
        }
    }

}
