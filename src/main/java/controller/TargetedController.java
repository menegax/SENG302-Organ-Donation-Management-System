package controller;

import model.User;

public abstract class TargetedController {

    User target;

    public void setTarget(User target) {
        this.target = target;
        loadController();
    }

    protected abstract void loadController();
}
