package com.mogukun.sentry.models;

import com.mogukun.sentry.Sentry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;


public class MovementData {

    public ArrayList<CollidingData> collidingData = new ArrayList<>();
    public ArrayList<Block> colliding = new ArrayList<>();
    public ArrayList<Block> standing = new ArrayList<>();

    public ArrayList<Block> hittingHead = new ArrayList<>();

    public ArrayList<Entity> collidingEntity = new ArrayList<>();

    public Player player;
    public double lastX = 0, lastY = 0, lastZ = 0;
    public float lastYaw = 0, lastPitch = 0;
    public double currentX = 0, currentY = 0, currentZ = 0;
    public float currentYaw = 0, currentPitch = 0;

    public double currentDeltaX = 0, currentDeltaY = 0, currentDeltaZ = 0, currentDeltaYaw = 0, currentDeltaPitch = 0, currentDeltaXZ = 0;
    public double lastDeltaX = 0, lastDeltaY = 0, lastDeltaZ = 0, lastDeltaYaw = 0, lastDeltaPitch = 0, lastDeltaXZ = 0;

    public int clientAirTick = 0, serverAirTick, clientGroundTick = 0, serverGroundTick = 0;

    public boolean clientGround = true, serverGround = true;
    public boolean isOnIce = false, isOnSlime = false;
    public int sinceIceTick = 0, sinceSlimeTick = 0, iceTick = 0, slimeTick = 0;

    public int sinceWaterTick = 0, waterTick = 0;

    public int sinceClimbTick = 0, climbTick = 0;

    public boolean isInLiquid = false, isInClimb = false;

    public boolean isInWeb = false;
    public int webTick = 0, sinceWebTick = 0;

    public boolean moving = false, rotating = false;

    public boolean isHittingHead = false, hasHorizontallyColliding = false;

    public boolean isOnBoat = false;

    public int standingOnBoatTick = 0, sinceStandingOnBoatTick = 0;
    public int vehicleTick = 0, sinceVehicleTick = 0;

    public boolean isOnStair = false;
    public int onStairTick = 0, sinceOnStairTick = 0;


    public double lastGroundY = 0, serverFallDistance = 0;

    public int teleportTick = 0, sinceVelocityTaken = 0, respawnTick = 0;

    public long ping = 0;

    public int movingTick = 0, rotatingTick = 0, sinceMovingTick = 0, sinceRotatingTick = 0;

    public MovementData() {}

    public MovementData(Player player,
                        double x, double y, double z, float yaw, float pitch,
                        boolean ground, boolean moving, boolean rotating,
                        MovementData lastMovementData) {

        this.player = player;
        this.ping = Sentry.instance.dataManager.getPlayerData(player).ping;

        teleportTick = Sentry.instance.dataManager.getPlayerData(player).teleportTick++;
        sinceVelocityTaken = Sentry.instance.dataManager.getPlayerData(player).sinceVelocityTakenTick++;
        respawnTick = Sentry.instance.dataManager.getPlayerData(player).respawnTick++;

        Sentry.instance.dataManager.getPlayerData(player).sinceFlying++;
        if ( player.isFlying() ) {
            Sentry.instance.dataManager.getPlayerData(player).sinceFlying = 0;
            Sentry.instance.dataManager.getPlayerData(player).backOnGroundSinceFly = false;
        }

        Location playerLoc =  new Location(player.getWorld(),x,y,z);

        colliding = getCollidingBlock( playerLoc );

        standing = getAroundBlock( playerLoc );
        hittingHead = getAboveBlock( new Location(player.getWorld(),x,y + 1 ,z) );

        ArrayList<Entity> collidingEntityTemp = getCollidingEntities(playerLoc);
        collidingEntity = collidingEntityTemp == null ? lastMovementData.collidingEntity : collidingEntityTemp;


        clientGround = ground;

        serverGround = false; // because it should set false for now.
        for ( Block block : standing ) {
            double by = block.getY();
            double pby = playerLoc.getBlockY();
            double py = playerLoc.getY();


            boolean onBlock = by == pby - 1
                    || ( by == pby && (py % (1D /64D) < 1E-4)  );

            if ( block.getType() != Material.AIR &&
                    ( block.getType().isSolid() || (block.getType().isBlock() && !block.isLiquid()) ) &&
                    onBlock ) {

                serverGround = true;
                Sentry.instance.dataManager.getPlayerData(player).backOnGroundSinceFly = true;
            }
            if (block.getType().toString().contains("ICE") && onBlock) {
                isOnIce = true;
            }
            if (block.getType().toString().contains("SLIME") && onBlock) {
                isOnSlime = true;
            }

            if(onBlock) collidingData.add( new CollidingData(block.getType().toString(), CollidingType.Standing) );
        }

        for ( Block block : hittingHead ) {
            boolean yep =  block.getType() != Material.AIR && ( block.getType().isSolid() || (block.getType().isBlock() && !block.isLiquid()) );
            if ( yep ) isHittingHead = true;
            if ( yep ) collidingData.add( new CollidingData(block.getType().toString(), CollidingType.Above) );
        }

        for ( Block block : colliding ) {
            if (block.isLiquid()) isInLiquid = true;
            boolean isOnSameHeight = block.getY() == playerLoc.getBlockY() ||
                    block.getY() == playerLoc.getBlockY() + 1;
            if( isOnSameHeight ) {
                if ( block.getType() != Material.AIR && block.getType().isSolid() ) hasHorizontallyColliding = true;
                collidingData.add( new CollidingData(block.getType().toString(), CollidingType.Horizontally) );
            }

            if( block.getType() == Material.LADDER || block.getType() == Material.VINE ) {
                isInClimb = true;
            }

            if ( block.getType() == Material.WEB ) {
                isInWeb = true;
            }

            if ( block.getType().toString().contains("STAIR") || block.getType().toString().contains("SLAB") ){
                isOnStair = true;
            }

            collidingData.add( new CollidingData(block.getType().toString(), CollidingType.Colliding) );

        }

        for ( Entity e : collidingEntity ) {
            if ( e.getType() == EntityType.BOAT ) isOnBoat = true;
        }


        this.moving = moving;
        this.rotating = rotating;

        if ( moving ) {
            movingTick = lastMovementData.movingTick + 1;
            sinceMovingTick = 0;
        } else {
            movingTick = 0;
            sinceMovingTick = lastMovementData.sinceMovingTick + 1;
        }

        if ( rotating ) {
            rotatingTick = lastMovementData.rotatingTick + 1;
            sinceRotatingTick = 0;
        } else {
            rotatingTick = 0;
            sinceRotatingTick = lastMovementData.sinceRotatingTick + 1;
        }


        currentX = x;
        currentY = y;
        currentZ = z;
        currentYaw = yaw;
        currentPitch = pitch;

        lastX = lastMovementData.currentX;
        lastY = lastMovementData.currentY;
        lastZ = lastMovementData.currentZ;
        lastYaw = lastMovementData.currentYaw;
        lastPitch = lastMovementData.currentPitch;


        currentDeltaX = Math.abs(currentX - lastX);
        currentDeltaY = Math.abs(currentY - lastY);
        currentDeltaZ = Math.abs(currentZ - lastZ);
        currentDeltaXZ = Math.hypot(Math.abs(currentDeltaX), Math.abs(currentDeltaZ));
        currentDeltaYaw = Math.abs(currentYaw - lastYaw);
        currentDeltaPitch = Math.abs(currentPitch - lastPitch);

        lastDeltaX = lastMovementData.currentDeltaX;
        lastDeltaY = lastMovementData.currentDeltaY;
        lastDeltaZ = lastMovementData.currentDeltaZ;
        lastDeltaXZ = lastMovementData.currentDeltaXZ;
        lastDeltaYaw = lastMovementData.currentDeltaYaw;
        lastDeltaPitch = lastMovementData.currentDeltaPitch;


        clientAirTick = lastMovementData.clientAirTick + 1;
        if ( clientGround ) clientAirTick = 0;

        serverAirTick = lastMovementData.serverAirTick + 1;
        if ( serverGround ) serverAirTick = 0;


        clientGroundTick = lastMovementData.clientGroundTick + 1;
        if ( !clientGround ) clientGroundTick = 0;

        serverGroundTick = lastMovementData.serverGroundTick + 1;
        if ( !serverGround ) serverGroundTick = 0;


        sinceIceTick = lastMovementData.sinceIceTick + 1;
        if ( isOnIce ) sinceIceTick = 0;

        sinceSlimeTick = lastMovementData.sinceSlimeTick + 1;
        if ( isOnSlime ) sinceSlimeTick = 0;

        iceTick = lastMovementData.iceTick + 1;
        if ( !isOnIce ) iceTick = 0;

        slimeTick = lastMovementData.slimeTick + 1;
        if ( !isOnSlime ) slimeTick = 0;

        waterTick = lastMovementData.waterTick + 1;
        if ( !isInLiquid ) waterTick = 0;

        sinceWaterTick = lastMovementData.sinceWaterTick + 1;
        if ( isInLiquid ) sinceWaterTick = 0;

        lastGroundY = lastMovementData.lastGroundY;
        if ( serverGround ) {
            lastGroundY = lastY;
        }

        climbTick = lastMovementData.climbTick + 1;
        if ( !isInClimb ) {
            climbTick = 0;
        }

        sinceClimbTick = lastMovementData.sinceClimbTick + 1;
        if ( isInClimb ) {
            sinceClimbTick = 0;
        }


        webTick = lastMovementData.webTick + 1;
        if ( !isInWeb ) {
            webTick = 0;
        }

        sinceWebTick = lastMovementData.sinceWebTick + 1;
        if ( isInWeb ) {
            sinceWebTick = 0;
        }

        standingOnBoatTick = lastMovementData.standingOnBoatTick + 1;
        if ( !isOnBoat ) {
            standingOnBoatTick = 0;
        }

        sinceStandingOnBoatTick = lastMovementData.sinceStandingOnBoatTick + 1;
        if ( isOnBoat ) {
            sinceStandingOnBoatTick = 0;
        }

        vehicleTick = lastMovementData.vehicleTick + 1;
        if ( player.getVehicle() == null ) {
            vehicleTick = 0;
        }

        sinceVehicleTick = lastMovementData.sinceVehicleTick + 1;
        if ( player.getVehicle() != null ) {
            sinceVehicleTick = 0;
        }

        onStairTick = lastMovementData.onStairTick + 1;
        if ( !isOnStair ) {
            onStairTick = 0;
        }

        sinceOnStairTick = lastMovementData.sinceOnStairTick + 1;
        if ( isOnStair ) {
            sinceOnStairTick = 0;
        }

        serverFallDistance = lastMovementData.serverFallDistance;
        if ( !serverGround && currentY < lastY ) {
            serverFallDistance += currentDeltaY;
        } else {
            serverFallDistance = 0;
        }

    }

    public ArrayList<Entity> getCollidingEntities(Location playerLocation) {
        List<Entity> entities;

        try {
            entities = new ArrayList<>(playerLocation.getWorld().getEntities()); // Shallow copy
        } catch (Exception ignore){
            return null;
        }

        ArrayList<Entity> temp = new ArrayList<>();

        try {
            for (Entity e : entities) {
                try {
                    Location el = e.getLocation().clone();
                    double dist = el.distance(playerLocation);
                    if (dist < 1.6 && !e.getUniqueId().equals(player.getUniqueId())) {
                        temp.add(e);
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception ignore){
            return temp;
        }

        return temp;
    }


    private ArrayList<Block> getCollidingBlock(Location playerLocation) {
        ArrayList<Block> collidingBlocks = new ArrayList<>();

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                for (int yOffset = -1; yOffset <= 2; yOffset++) {

                    Block block = playerLocation.getWorld().getBlockAt(
                            playerLocation.getBlockX() + xOffset,
                            playerLocation.getBlockY() + yOffset,
                            playerLocation.getBlockZ() + zOffset
                    );

                    if (isColliding(playerLocation, block) ||
                            isColliding(playerLocation.clone().add(0,1,0), block) ||
                            isColliding(playerLocation.clone().add(0,2,0), block)) {
                        collidingBlocks.add(block);
                    }
                }
            }
        }

        return collidingBlocks;
    }


    private ArrayList<Block> getAboveBlock(Location playerLocation) {

        ArrayList<Block> collidingBlocks = new ArrayList<>();

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                for (int yOffset = 0; yOffset <= 2; yOffset++) {

                    Block block = player.getWorld().getBlockAt(
                            playerLocation.getBlockX() + xOffset,
                            playerLocation.getBlockY() + yOffset,
                            playerLocation.getBlockZ() + zOffset
                    );

                    if (isColliding(playerLocation.clone().add(0,1,0), block)) collidingBlocks.add(block);
                }
            }
        }

        return collidingBlocks;
    }



    private ArrayList<Block> getAroundBlock(Location playerLocation) {

        ArrayList<Block> collidingBlocks = new ArrayList<>();

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                for(int yOffset = 0; yOffset <= 1; yOffset++ ){

                    Block block = player.getWorld().getBlockAt(
                            playerLocation.getBlockX() + xOffset,
                            playerLocation.getBlockY() - yOffset,
                            playerLocation.getBlockZ() + zOffset
                    );

                    if(isColliding(playerLocation, block)) collidingBlocks.add(block);
                }
            }
        }

        return collidingBlocks;
    }

    private boolean isColliding(Location playerLocation, Block block){

        Vector playerPos = playerLocation.toVector();

        double checkY = 0.5 + ( block.getType().toString().contains("FENCE") ? 0.5 : 0 );

        double feetY = playerPos.getY() - checkY;

        double blockMinX = block.getX() - 1.5;
        double blockMaxX = block.getX() + 1.5;
        double blockMinY = block.getY() - checkY;
        double blockMaxY = block.getY() + checkY;
        double blockMinZ = block.getZ() - 1.5;
        double blockMaxZ = block.getZ() + 1.5;

        boolean isColliding = playerPos.getX() >= blockMinX && playerPos.getX() <= blockMaxX &&
                playerPos.getZ() >= blockMinZ && playerPos.getZ() <= blockMaxZ &&
                feetY >= blockMinY && feetY <= blockMaxY;

        Location locBlock = block.getLocation();
        locBlock = locBlock.clone().add(0.5, 0.5, 0.5);
        Location locPlayer = playerLocation.clone();
        locPlayer.setY(locBlock.getY());

        double distance = Math.sqrt(
                Math.pow(locBlock.getX() - locPlayer.getX(), 2) +
                        Math.pow(locBlock.getZ() - locPlayer.getZ(), 2)
        );

        return isColliding && distance <= 1.14;
    }

    public boolean isCollidingTo(String s, CollidingType t) {
        for ( CollidingData d : collidingData ) {
            if ( d.name.toLowerCase().contains(s.toLowerCase()) && d.type == t ) return true;
        }
        return false;
    }

}
