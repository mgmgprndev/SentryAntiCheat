package com.mogukun.sentry.listeners;

import com.mogukun.sentry.Sentry;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;

public final class PacketListener extends ChannelDuplexHandler {

    @Override
    public void write(final ChannelHandlerContext channelHandlerContext, final Object object, final ChannelPromise channelPromise) throws Exception {
        super.write(channelHandlerContext, object, channelPromise);

        try {
            final Packet<PacketListenerPlayOut> packet = (Packet<PacketListenerPlayOut>) object;

            // Get the player who sent the packet
            EntityPlayer player = getPlayer(channelHandlerContext);
            if (player != null) {
                Sentry.instance.checkManager.runCheck(player.getBukkitEntity(), packet);
            }

        }
        catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext channelHandlerContext, final Object object) throws Exception {
        super.channelRead(channelHandlerContext, object);

        try {
            final Packet<PacketListenerPlayIn> packet = (Packet<PacketListenerPlayIn>) object;

            EntityPlayer player = getPlayer(channelHandlerContext);
            if (player != null) {
                Sentry.instance.checkManager.runCheck(player.getBukkitEntity(), packet);
            }

        }
        catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private EntityPlayer getPlayer(ChannelHandlerContext ctx) {
        return null; // fuck no idda.
    }


}
