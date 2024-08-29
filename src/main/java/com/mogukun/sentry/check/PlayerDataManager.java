package com.mogukun.sentry.check;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {

    public HashMap<UUID, PlayerData> playerDataHashMap;

    public PlayerDataManager() {
        playerDataHashMap = new HashMap<>();
    }

    public PlayerData getPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        playerDataHashMap.computeIfAbsent( uuid, k -> new PlayerData() );
        return playerDataHashMap.get( uuid );
    }

}
