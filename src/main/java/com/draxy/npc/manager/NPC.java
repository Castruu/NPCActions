package com.draxy.npc.manager;

import com.draxy.npc.actions.ActionsEnum;
import net.minecraft.server.v1_16_R2.EntityPlayer;

public class NPC {

    private final EntityPlayer npc;
    private final String name;



    private ActionsEnum action;

    public NPC(EntityPlayer npc, String name, ActionsEnum action) {
        this.npc = npc;
        this.name = name;
        this.action = action;
    }



    public EntityPlayer getNPC() {
        return npc;
    }

    public String getName() {
        return name;
    }

    public void setAction(ActionsEnum action) {
        this.action = action;
    }

    public ActionsEnum getAction() {
        return action;
    }

}
