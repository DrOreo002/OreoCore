package me.droreo002.oreocore.netty.app;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Getter;
import lombok.SneakyThrows;
import me.droreo002.oreocore.netty.NettyConnectionCallback;
import org.jetbrains.annotations.NotNull;

public class SimpleTextClient extends NettyApplication {

    @Getter
    private EventLoopGroup loopGroup;
    @Getter
    private NettyConnectionCallback connectionCallback;

    public SimpleTextClient(String name, String ip, int port) {
        super(name, ip, port, Type.CLIENT);
    }

    @SneakyThrows
    public void start(@NotNull NettyConnectionCallback connectionCallback) {
        this.loopGroup = new NioEventLoopGroup();
        this.connectionCallback = connectionCallback;

        Bootstrap bootstrap = new Bootstrap()
                .group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addLast("frame", new LineBasedFrameDecoder(1024, true, true));
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());

                        pipeline.addLast("handler", new SimpleChannelInboundHandler<String>() {

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                connectionCallback.onChannelConnected(ctx);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) {
                                connectionCallback.onChannelDisconnected(ctx);
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
                                connectionCallback.onReceived(channelHandlerContext.channel(), socketChannel, s);
                                if (getPacketHandler() != null) {
                                    String response = getPacketHandler().handle(s);
                                    if (response != null) channelHandlerContext.writeAndFlush(response);
                                }
                            }
                        });
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = bootstrap.connect(getIp(), getPort()).sync();

        connectionCallback.onStart();
        // Wait until connection is closed
        future.channel().closeFuture().sync();
    }

    @Override
    public void stop() {
        loopGroup.shutdownGracefully();
        connectionCallback.onStop();
    }
}
