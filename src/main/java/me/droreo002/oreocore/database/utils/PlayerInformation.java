package me.droreo002.oreocore.database.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerInformation {
    private String playerName;
    private UUID playerUuid;
}
