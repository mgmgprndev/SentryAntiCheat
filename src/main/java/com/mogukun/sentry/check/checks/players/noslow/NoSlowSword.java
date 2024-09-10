package com.mogukun.sentry.check.checks.players.noslow;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import com.mogukun.sentry.models.Counter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@CheckInfo(
        name = "NoSlow (Sword)",
        description = "Blocking with Sword while sprinting",
        category = Category.PLAYER
)
public class NoSlowSword extends Check {

    Counter counter = new Counter();

    boolean isBlocking = false;
    boolean isSprinting = false;

    @Override
    public void handle(Packet thePacket) {

        if ( thePacket instanceof PacketPlayInEntityAction ) {
            PacketPlayInEntityAction packet = (PacketPlayInEntityAction) thePacket;

            if ( packet.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING
                    || packet.b() == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING ) {
                isSprinting = packet.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING;
            }

        }

        if ( thePacket instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) thePacket;
            if ( packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT && isHandHoldSword() ) {
                isBlocking = true;
            }
        }

        if ( thePacket instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig packet = (PacketPlayInBlockDig) thePacket;
            if ( packet.c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM && isBlocking ){
                isBlocking = false;
            }
        }

        if ( thePacket instanceof PacketPlayInBlockPlace && isHandHoldSword() ) {
            isBlocking = true;
        }


        if ( thePacket instanceof PacketPlayInFlying) {
            if ( !isHandHoldSword() ) {
                isBlocking = false;
            }

            if ( isSprinting && isBlocking ) {
                if ( counter.count() > 5 ) flag();
            }
        }


    }

    private boolean isHandHoldSword() {
        if ( player.getItemInHand()  == null ) return false;
        return player.getItemInHand().getType().toString().contains("SWORD");
    }
}
