package me.droreo002.oreocore.netty;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PacketData {

    @Getter @Setter
    private String header;
    @Getter
    private String message;
    @Getter
    @Nullable
    private Class<?>[] packetParams;
    @Getter
    private int packetLength; // Packet length are determined by the amount of message after header

    public PacketData(@NotNull String message, @Nullable Class<?>... packetParams) {
        this.message = message;
        this.packetParams = packetParams;
        this.packetLength = (packetParams == null) ? 1 : packetParams.length + 1;
    }

    /**
     * Build this packet message
     *
     * @param params The parameter to add
     * @return Result string
     */
    public String build(@Nullable Object... params) {
        if (packetParams != null) {
            StringBuilder builder = new StringBuilder();
            if (params == null) throw new InvalidTextPacketException("This packet require parameters to be specified!");
            if (params.length != packetParams.length) throw new InvalidTextPacketException("Invalid parameters given!");
            for (int i = 0; i < packetParams.length; i++) {
                Class<?> found = params[i].getClass();
                Class<?> required = packetParams.getClass();
                if (!found.isAssignableFrom(required)) throw new InvalidTextPacketException("Invalid packet type given!. Required " + required.getName() + " found " + found.getName());
                builder.append(required.cast(params[i]));
            }
            return builder.toString();
        } else {
            return message;
        }
    }

    /**
     * Called when this packet is received
     *
     * @param packetString The packet string
     * @return The packet response
     */
    @Nullable
    public abstract String onReceived(String packetString);

    /**
     * Called when the packet is sent
     */
    public abstract void onSent();

    /**
     * Check if the packetStr is a valid instance
     * of this packet
     *
     * @param packetStr The packet string to check
     * @return true if valid, false otherwise
     */
    public boolean isValid(String packetStr) {
        if (!packetStr.startsWith(header + "|")) return false;
        String[] data = packetStr.split(header + "\\|");
        return data[0].equals(message);
    }
}