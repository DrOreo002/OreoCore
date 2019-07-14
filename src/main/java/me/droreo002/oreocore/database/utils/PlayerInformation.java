package me.droreo002.oreocore.database.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class PlayerInformation {
    private String playerName;
    private UUID playerUuid;
}
