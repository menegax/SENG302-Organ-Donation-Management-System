package controller;

import model.User;

public abstract class TargetedController {

    protected User target;

    public void setTarget(User target) {
        this.target = target;
        load();
    }

    abstract protected void load();
}
