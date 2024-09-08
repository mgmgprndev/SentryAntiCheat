package com.mogukun.sentry.check;

import com.mogukun.sentry.Sentry;
import org.bukkit.GameMode;
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

    public boolean isBypass() {
        GameMode gm = player.getGameMode();

        if ( gm == GameMode.SPECTATOR ) {
            return true;
        }

        if ( Sentry.instance.dataManager.getPlayerData(player).sinceFlying < 20  ) {
            return true;
        }

        if ( !Sentry.instance.dataManager.getPlayerData(player).backOnGroundSinceFly ) {
            return true;
        }

        if ( player.isDead() ) {
            return true;
        }

        return false;

    }





}
