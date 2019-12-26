package me.droreo002.oreocore.utils.multisupport;

import lombok.Getter;
import me.droreo002.oreocore.enums.MinecraftVersion;

import static me.droreo002.oreocore.enums.MinecraftVersion.*;

public enum MinecraftFeature {

    CUSTOM_MODEL_DATA(V1_14_R1, V1_15_R1),
    NEW_MATERIAL_NAME(V1_13_R1, V1_13_R2, V1_14_R1, V1_15_R1);

    @Getter
    private MinecraftVersion[] onVersion;

    MinecraftFeature(MinecraftVersion... onVersion) {
        this.onVersion = onVersion;
    }
}
