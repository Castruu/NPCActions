package com.draxy.npc.actions;

import com.draxy.npc.manager.NPC;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Actions {

    public abstract void action(NPC npc);

    protected abstract BukkitRunnable getRunnable(NPC npc);
}
