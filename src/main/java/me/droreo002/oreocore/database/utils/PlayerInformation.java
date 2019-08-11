package me.droreo002.oreocore.database.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerInformation {
    private String playerName;
    private UUID playerUuid;

    public PlayerInformation(Player player) {
        this.playerName = player.getName();
        this.playerUuid = player.getUniqueId();
    }
}
