package me.droreo002.oreocore.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.jetbrains.annotations.Nullable;

public interface NettyConnectionCallback {

    /**
     * Called when there's a channel
     * that disconnected
     *
     * @param ctx Handler
     */
    void onChannelDisconnected(ChannelHandlerContext ctx);

    /**
     * Called when there's a new channel
     * that connected
     *
     * @param ctx Handler
     */
    void onChannelConnected(ChannelHandlerContext ctx);

    /**
     * Called when netty instance is started
     */
    void onStart();

    /**
     * Called when netty instance is stopped
     */
    void onStop();

    /**
     * Called when netty instance received
     * a data
     *
     * @param sender The channel sender
     * @param receiver The channel receiver, null if received on server side
     * @param message The message that sender sent
     */
    void onReceived(Channel sender, @Nullable SocketChannel receiver, String message);
}
