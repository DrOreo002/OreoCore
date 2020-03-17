package me.droreo002.oreocore.netty.app;

import lombok.Getter;
import me.droreo002.oreocore.netty.PacketContainer;
import me.droreo002.oreocore.netty.PacketData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PacketHandler {

    @Getter
    private List<PacketContainer> registeredPackets;
    @Getter
    private PacketContainer disconnectPacket;

    public PacketHandler() {
        this.registeredPackets = new ArrayList<>();
    }

    /**
     * Register a packet
     *
     * @param packet The packet to register
     */
    public void registerPacketContainer(PacketContainer packet) {
        this.registeredPackets.add(packet);
    }

    /**
     * Register a disconnect packet
     *
     * @param header The header
     */
    public void registerOnDisconnectPacket(String header) {
        PacketContainer container = new PacketContainer(header);
        container.addPacketMessage(new PacketData("Disconnect") {
            @NotNull
            @Override
            public String onReceived(String packetString) {
                return "Disconnect";
            }

            @Override
            public void onSent() {

            }
        });
        this.disconnectPacket = container;
    }

    /**
     * Handle the packet string
     *
     * @param packetString The packet string to handle
     * @return String response if there's any
     */
    @Nullable
    public String handle(String packetString) {
        PacketData packetData = null;
        for (PacketContainer packet : getRegisteredPackets()) {
            packetData = packet.getPacketData(packetString);
        }
        if (packetData == null) return null;
        return packetData.onReceived(packetString);
    }
}
