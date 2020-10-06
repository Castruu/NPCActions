package com.draxy.npc.actions;

public enum ActionsEnum {

    JUMP("&7Jumping"),
    STANDING("&7Standing"),
    CROUCH("&7Crouching"),
    ATTACKING("&7Attacking");

    private final String actionName;

    ActionsEnum(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }

}
