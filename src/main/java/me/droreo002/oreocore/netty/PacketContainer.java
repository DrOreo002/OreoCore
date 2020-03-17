package me.droreo002.oreocore.netty;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacketContainer {

    @Getter
    private String header;
    @Getter
    private List<PacketData> packetData;

    public PacketContainer(String header) {
        this.header = header;
        this.packetData = new CopyOnWriteArrayList<>();
    }

    public void addPacketMessage(PacketData message) {
        message.setHeader(header);
        this.packetData.add(message);
    }

    /**
     * Get packet data from string
     *
     * @param s The string
     * @return PacketData if there's any
     */
    @Nullable
    public PacketData getPacketData(String s) {
        for (PacketData message : packetData) {
            if (message.isValid(s)) return message;
        }
        return null;
    }
}
