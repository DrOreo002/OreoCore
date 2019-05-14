package me.droreo002.oreocore.enums;

public enum Currency {

    THOUSANDS,
    MILLIONS,
    TRILLIONS,
    BILLIONS;

    public static Currency getCurrency(String name) {
        Currency currency;
        try {
            currency = Currency.valueOf(name);
        } catch (Exception e) {
            // Ignore
            return null;
        }
        return currency;
    }
}
