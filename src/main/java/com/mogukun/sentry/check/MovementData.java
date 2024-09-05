package com.mogukun.sentry.check;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class MovementData {

    public ArrayList<Block> colliding = new ArrayList<>();
    public ArrayList<Block> standing = new ArrayList<>();

    public ArrayList<Block> hittingHead = new ArrayList<>();

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

    public boolean moving = false, rotating = false;

    public boolean isHittingHead = false, hasHorizontallyColliding = false;


    public double lastGroundY = 0, serverFallDistance = 0;

    public MovementData() {}

    public MovementData(Player player,
                        double x, double y, double z, float yaw, float pitch,
                        boolean ground, boolean moving, boolean rotating,
                        MovementData lastMovementData) {

        this.player = player;

        Location playerLoc =  new Location(player.getWorld(),x,y,z);
        colliding = getCollidingBlock( playerLoc );
        standing = getAroundBlock( playerLoc );
        hittingHead = getAboveBlock( new Location(player.getWorld(),x,y + 1 ,z) );

        clientGround = ground;

        serverGround = false; // because it should set false for now.
        for ( Block block : standing ) {
            double by = block.getY();
            double pby = playerLoc.getBlockY();
            double py = playerLoc.getY();


            boolean onBlock = by == pby - 1
                    || ( by == pby && (py % (1/64D) < 1E-4)  ) ;


            if ( block.getType() != Material.AIR && block.getType().isSolid() && onBlock ) {
                serverGround = true;
            }
            if (block.getType().toString().contains("ICE") && onBlock) {
                isOnIce = true;
            }
            if (block.getType().toString().contains("SLIME") && onBlock) {
                isOnSlime = true;
            }
        }

        for ( Block block : hittingHead ) {
            if ( block.getType() != Material.AIR && block.getType().isSolid()
                    && block.getY() == playerLoc.getBlockY() + 2  ) isHittingHead = true;
        }

        for ( Block block : colliding ) {
            if (block.isLiquid()) isInLiquid = true;
            boolean isOnSameHeight = block.getY() == playerLoc.getBlockY() ||
                    block.getY() == playerLoc.getBlockY() + 1;
            if( isOnSameHeight ) {
                if ( block.getType() != Material.AIR && block.getType().isSolid() ) hasHorizontallyColliding = true;
            }

            if( block.getType() == Material.LADDER || block.getType() == Material.VINE ) {
                isInClimb = true;
            }
        }


        this.moving = moving;
        this.rotating = rotating;



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


        serverFallDistance = lastMovementData.serverFallDistance;
        if ( !serverGround && currentY < lastY ) {
            serverFallDistance += currentDeltaY;
        } else {
            serverFallDistance = 0;
        }

    }

    private ArrayList<Block> getCollidingBlock(Location playerLocation) {

        ArrayList<Block> collidingBlocks = new ArrayList<>();

        // Iterate over the blocks around the player's head and legs
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                for (int yOffset = -1; yOffset <= 2; yOffset++) { // from -1 (above head) to 2 (below feet)

                    Block block = playerLocation.getWorld().getBlockAt(
                            playerLocation.getBlockX() + xOffset,
                            playerLocation.getBlockY() + yOffset,
                            playerLocation.getBlockZ() + zOffset
                    );

                    if (isColliding(playerLocation, block)) {
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
                for (int yOffset = -1; yOffset <= 1; yOffset++) { // Changed yOffset range to include negative values

                    Block block = player.getWorld().getBlockAt(
                            playerLocation.getBlockX() + xOffset,
                            playerLocation.getBlockY() + yOffset, // Note the change to use yOffset directly
                            playerLocation.getBlockZ() + zOffset
                    );

                    if (isColliding(playerLocation, block)) collidingBlocks.add(block);
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

        double checkY = 0.5;

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

        // if ( isColliding && block.getType().isSolid() ) player.sendMessage("distance=" + distance);

        return isColliding && distance <= 1.14;
    }

}
