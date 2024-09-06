package com.mogukun.sentry.listeners;

import com.mogukun.sentry.Sentry;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;

public final class PacketHandler extends ChannelDuplexHandler {

    public Player player;

    public PacketHandler(Player player) {
        this.player = player;
    }

    @Override
    public void write(final ChannelHandlerContext channelHandlerContext, final Object object, final ChannelPromise channelPromise) throws Exception {
        super.write(channelHandlerContext, object, channelPromise);

        try {
            final Packet<PacketListenerPlayOut> packet = (Packet<PacketListenerPlayOut>) object;
            Sentry.instance.checkManager.runCheck(player, packet);

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

            Sentry.instance.checkManager.runCheck(player, packet);

        }
        catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }



}
