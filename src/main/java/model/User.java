package model;

import java.sql.Timestamp;
import java.util.UUID;

abstract class User {

    private final UUID uuid = UUID.randomUUID();

    private boolean changed = true;
    
    protected Timestamp modified;
    
    public boolean getChanged() {
    	return changed;
    }
    
    protected void setChanged() {
        modified = new Timestamp(System.currentTimeMillis());
        changed = true;
    }
    
    public UUID getUuid() {
        return uuid;
    }

}
