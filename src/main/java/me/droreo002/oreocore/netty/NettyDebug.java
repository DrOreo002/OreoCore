package me.droreo002.oreocore.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import me.droreo002.oreocore.netty.app.PacketHandler;
import me.droreo002.oreocore.netty.app.SimpleTextClient;
import me.droreo002.oreocore.netty.app.SimpleTextServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NettyDebug {

    public static void startDebug() {
        PacketHandler handler = new PacketHandler();
        PacketContainer universalPacketContainer = new PacketContainer("OreoCore");

        universalPacketContainer.addPacketMessage(new PacketData("Hello") {
            @NotNull
            @Override
            public String onReceived(String packetString) {
                return "World";
            }

            @Override
            public void onSent() {

            }
        });

        handler.registerPacketContainer(universalPacketContainer);

        System.out.println("Running instances");
        SimpleTextClient client = new SimpleTextClient("Client", "localhost", 9999);
        SimpleTextServer server = new SimpleTextServer("Server", "localhost", 9999);
        client.setPacketHandler(handler);
        server.setPacketHandler(handler);

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.start(new NettyConnectionCallback() {

                    @Override
                    public void onChannelDisconnected(ChannelHandlerContext ctx) {
                        System.out.println("A new client has connected " + ctx.channel().remoteAddress());
                    }

                    @Override
                    public void onChannelConnected(ChannelHandlerContext ctx) {
                        System.out.println("A client has disconnected " + ctx.channel().remoteAddress());
                    }

                    @Override
                    public void onStart() {
                        System.out.println("Server started");
                    }

                    @Override
                    public void onStop() {
                        System.out.println("Server stop!");
                    }

                    @Override
                    public void onReceived(Channel sender, @Nullable SocketChannel receiver, String message) {
                        System.out.println("[Server] Received message: " + message);
                    }
                });
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                client.start(new NettyConnectionCallback() {

                    @Override
                    public void onChannelDisconnected(ChannelHandlerContext ctx) {
                        System.out.println("Client disconnected from server");
                    }

                    @Override
                    public void onChannelConnected(ChannelHandlerContext ctx) {
                        System.out.println("Client connected to server " + ctx.channel().remoteAddress());
                    }

                    @Override
                    public void onStart() {
                        System.out.println("Client started!");
                    }

                    @Override
                    public void onStop() {
                        System.out.println("Client stopped!");
                    }

                    @Override
                    public void onReceived(Channel sender, @Nullable SocketChannel receiver, String message) {
                        System.out.println("[Client] Received message: " + message);
                    }
                });
            }
        }).start();
    }
}
