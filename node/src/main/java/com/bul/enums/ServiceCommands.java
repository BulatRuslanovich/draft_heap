package com.bul.enums;

public enum ServiceCommands {
    HELP("/help"),
    START("/start"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    GET_STICKER("/get_sticker");

    private final String value;

    ServiceCommands(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommands fromValue(String val) {
        for (var cmd : ServiceCommands.values()) {
            if (cmd.value.equals(val)) {
                return cmd;
            }
        }

        return null;
    }

}
