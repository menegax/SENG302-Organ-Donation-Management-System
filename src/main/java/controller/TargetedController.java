package controller;

import model.User;

public abstract class TargetedController {

    User target;

    public void setTarget(User target) {
        this.target = target;
        load();
    }

    protected abstract void load();
}
