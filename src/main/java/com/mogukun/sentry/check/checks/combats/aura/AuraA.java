package com.mogukun.sentry.check.checks.combats.aura;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.EntityHitData;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.UUID;

@CheckInfo(
        name = "KillAura (A)",
        path = "combat.killaura.a",
        description = "Multi Aura Detection",
        category = Category.COMBAT
)
public class AuraA extends Check {


    ArrayList<EntityHitData> hits = new ArrayList<>();

    @Override
    public void event(Event event){
        if ( event instanceof EntityDamageByEntityEvent ) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            long now = System.currentTimeMillis();
            UUID uuid = e.getEntity().getUniqueId();

            hits.add( new EntityHitData(uuid) );
            hits.removeIf(h-> now - h.timestamp > 300);

            double sus = 0;
            int entity = 0;

            ArrayList<UUID> uuids = new ArrayList<>();
            for ( EntityHitData d : hits ) {
                double diff = now - d.timestamp;
                sus += diff;
                if ( !uuids.contains(d.uuid) ) {
                    entity++;
                    uuids.add(d.uuid);
                }
            }

            double x = sus / entity;

            double min = (35.2 * entity) - 5;

            if ( entity > 4 && x < min ) {
                flag( x + " < " + min );
            }

        }
    }

}
