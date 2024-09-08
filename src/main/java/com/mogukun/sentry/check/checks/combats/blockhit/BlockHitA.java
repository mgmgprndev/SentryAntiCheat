package com.mogukun.sentry.check.checks.combats.blockhit;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.Counter;
import net.minecraft.server.v1_8_R3.*;

@CheckInfo(
        name = "BlockHit (A)",
        description = "Block Hit",
        category = Category.COMBAT
)
public class BlockHitA extends Check {

    boolean isBlocking = false;

    Counter counter = new Counter();

    @Override
    public void handle(Packet thePacket) {


        if ( thePacket instanceof PacketPlayInUseEntity ) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) thePacket;
            if ( packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK ) {
                if ( isBlocking ) {
                    if ( counter.count() > 5 ) flag();
                }
            }
            if ( packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT ) {
                if(isHandHoldSword()) {
                    isBlocking = true;
                }
            }
        }

        if ( thePacket instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig packet = (PacketPlayInBlockDig) thePacket;
            if ( packet.c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM && isBlocking ){
                isBlocking = false;
            }
        }

        if ( thePacket instanceof PacketPlayInBlockPlace) {
            if(isHandHoldSword()) {
                isBlocking = true;
            }
        }
    }

    private boolean isHandHoldSword() {
        if ( player.getItemInHand()  == null ) return false;
        return player.getItemInHand().getType().toString().contains("SWORD");
    }

}
