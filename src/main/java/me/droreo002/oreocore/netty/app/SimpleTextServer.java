package me.droreo002.oreocore.netty.app;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Getter;
import lombok.SneakyThrows;
import me.droreo002.oreocore.netty.NettyConnectionCallback;
import org.jetbrains.annotations.NotNull;

public class SimpleTextServer extends NettyApplication {

    @Getter
    private EventLoopGroup bossGroup, workerGroup;
    @Getter
    private NettyConnectionCallback connectionCallback;

    public SimpleTextServer(String name, String ip, int port) {
        super(name, ip, port, Type.SERVER);
    }

    @Override
    @SneakyThrows
    public void start(@NotNull NettyConnectionCallback connectionCallback) {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.connectionCallback = connectionCallback;

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

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
                                connectionCallback.onReceived(channelHandlerContext.channel(), null, s);
                                if (getPacketHandler() != null) {
                                    String response = getPacketHandler().handle(s);
                                    if (response != null) channelHandlerContext.writeAndFlush(response);
                                }
                            }
                        });
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = bootstrap.bind(getPort()).sync();

        connectionCallback.onStart();
        // Wait until the server socket is closed
        future.channel().closeFuture().sync();
    }

    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        connectionCallback.onStop();
    }
}
