package com.mogukun.sentry.check;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MovementData {

    public Player player;
    public double lastX = 0, lastY = 0, lastZ = 0;
    public float lastYaw = 0, lastPitch = 0;
    public double currentX = 0, currentY = 0, currentZ = 0;
    public float currentYaw = 0, currentPitch = 0;

    public double currentDeltaX = 0, currentDeltaY = 0, currentDeltaZ = 0, currentDeltaYaw = 0, currentDeltaPitch = 0, currentDeltaXZ = 0;
    public double lastDeltaX = 0, lastDeltaY = 0, lastDeltaZ = 0, lastDeltaYaw = 0, lastDeltaPitch = 0, lastDeltaXZ = 0;

    public int clientAirTick = 0, serverAirTick = 0;

    public boolean clientGround = true, serverGround = true;

    public boolean moving = false, rotating = false;

    public MovementData() {}

    public MovementData(Player player,
                        double x, double y, double z, float yaw, float pitch,
                        boolean ground, boolean moving, boolean rotating,
                        MovementData lastMovementData) {

        this.player = player;
        clientGround = ground;
        serverGround = isStandingOnBlock( new Location(player.getWorld(),x,y,z) );
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


        clientAirTick = lastMovementData.clientAirTick += 1;
        if ( clientGround ) clientAirTick = 0;

        serverAirTick = lastMovementData.serverAirTick += 1;
        if ( serverGround ) serverAirTick = 0;
    }


    private boolean isStandingOnBlock(Location playerLocation) {
        Vector playerPos = playerLocation.toVector();

        double checkY = 0.5;

        double feetY = playerPos.getY() - checkY;

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                for(int yOffset = 0; yOffset <= 1; yOffset++ ){

                    Block block = player.getWorld().getBlockAt(
                            playerLocation.getBlockX() + xOffset,
                            playerLocation.getBlockY() - yOffset,
                            playerLocation.getBlockZ() + zOffset
                    );

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
                    locBlock.clone().add(0.5,0.0,0.5);


                    double distance = locBlock.distance(playerLocation);


                    if (isColliding && block.getType() != Material.AIR && block.getType().isSolid() && distance <= 1.8 ) {
                        return true;
                    }

                }
            }
        }

        return false;
    }

}
