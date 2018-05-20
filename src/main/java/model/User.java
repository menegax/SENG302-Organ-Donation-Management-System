package model;

import java.util.UUID;

abstract class User {

    private final UUID uuid = UUID.randomUUID();

    public UUID getUuid() {
        return uuid;
    }

}
