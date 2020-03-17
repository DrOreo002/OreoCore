package me.droreo002.oreocore.netty.app;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.netty.NettyConnectionCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NettyApplication {

    @Getter
    private String name;
    @Getter
    private String ip;
    @Getter
    private int port;
    @Getter
    private Type type;
    @Getter @Setter
    @Nullable
    private PacketHandler packetHandler;

    public NettyApplication(String name, String ip, int port, Type type) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.type = type;
    }

    public void sendPacket(String packet, Channel channel) {
        channel.writeAndFlush(packet);
    }

    public void disconnect(Channel channel) {
        if (packetHandler != null) {
            sendPacket(packetHandler.getDisconnectPacket().getPacketData("Disconnect").build((Object) null), channel);
        }
        channel.close();
    }

    public abstract void start(@NotNull NettyConnectionCallback callback);
    public abstract void stop();

    public enum Type {
        SERVER,
        CLIENT
    }
}
