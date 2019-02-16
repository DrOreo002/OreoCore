package me.droreo002.oreocore.enums;

public enum  ArmorStandBody {

    HEAD,
    BODY,
    L_ARM,
    R_ARM,
    L_LEG,
    R_LEG;

    public static ArmorStandBody getBody(String name) {
        ArmorStandBody bod;
        try {
            bod = ArmorStandBody.valueOf(name);
        } catch (Exception e) {
            // Ignore
            return null;
        }
        return bod;
    }
}
