package com.mogukun.sentry.check.checks.combats.reach;

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
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "Reach (A)",
        description = "A Shit Reach Check",
        category = Category.COMBAT,
        experimental = true
)
public class ReachA extends Check {

    // track for all entities location that in around 6 blocks from certain player
    // for do this, everytime flying packet was sent,
    // get all near entities to add on hashmap + array list with entityUUID<ARRAY<LocationTimeStamp>>. or simply arraylist<LocationTimeStamp>
    //
    // and with time stamp, save location exact time
    // on attack, look for player's hit entity event and lookup the entity got hit (in near few ticks)
    // get most nearest distance.
    // make sure it is distance > 3.2
    //

    ConcurrentLinkedDeque<LocationTimeStamp> locationTimeStamps = new ConcurrentLinkedDeque<>();

    @Override
    public void handle(MovementData data)
    {
        Location playerLocation = player.getLocation().clone();

        for ( Entity ent : player.getWorld().getEntities() ) {
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

                long diff = now - lts.timestamp;
                if ( diff < 250 ) {
                    double dist = loc.distance(lts.loc);
                    if ( minimumDistance > dist ) minimumDistance = dist;
                }
            }

            if ( minimumDistance == Double.MAX_VALUE ) return;

            if ( minimumDistance > 3.5 ) {
                flag("distance=" + minimumDistance);
            }
        }
    }

}
