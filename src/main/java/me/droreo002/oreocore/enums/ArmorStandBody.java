package me.droreo002.oreocore.enums;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public enum  ArmorStandBody {

    HEAD("Head"),
    BODY("Body"),
    LEFT_ARM("LeftArm"),
    RIGHT_ARM("RightArm"),
    LEFT_LEG("LeftLeg"),
    RIGHT_LEG("RightLeg");

    @Getter
    private String asString;

    ArmorStandBody(String asString) {
        this.asString = asString;
    }

    /**
     * Get the ArmorStand body by string
     *
     * @param string Accepted are same as the enum or NBT values
     * @return ArmorStand body
     */
    @Nullable
    public static ArmorStandBody getBody(String string) {
        for (ArmorStandBody body : values()) {
            if (body.asString.equalsIgnoreCase(string)) return body;
        }
        return null;
    }
}
