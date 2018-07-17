package model;

import java.util.UUID;

public abstract class User {

    private final UUID uuid = UUID.randomUUID();

    public UUID getUuid() {
        return uuid;
    }

    public abstract String getNameConcatenated();

    public abstract String getUserType();

}
