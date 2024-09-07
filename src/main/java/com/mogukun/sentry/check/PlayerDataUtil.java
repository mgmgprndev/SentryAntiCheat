package com.mogukun.sentry.check;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
public class PlayerDataUtil {

    public Player player;

    public PlayerDataUtil(Player player) {
        this.player = player;
    }

    public int getAmplifier(PotionEffectType type) {
        for ( PotionEffect e : player.getActivePotionEffects() ) {
            if ( e.getType().equals(type) ) return e.getAmplifier();
        }
        return -1;
    }





}
