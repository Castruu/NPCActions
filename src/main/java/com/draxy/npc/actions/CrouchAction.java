package com.draxy.npc.actions;

import com.draxy.npc.NPCActions;
import com.draxy.npc.manager.NPC;
import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class CrouchAction extends Actions {
    private final long intervalInTicks;

    public CrouchAction(long intervalInMS) {
        double timeInSeconds = intervalInMS/1000.0;
        double toCeil = timeInSeconds*20.0;
        this.intervalInTicks = (long) Math.ceil(toCeil);
    }

    @Override
    public void action(NPC npc) {
        getRunnable(npc).runTaskTimerAsynchronously(NPCActions.getInstance(), 0, intervalInTicks);
    }

    @Override
    protected BukkitRunnable getRunnable(NPC npc) {
        EntityPlayer npcEntity = npc.getNPC();
        DataWatcher dataWatcher = npcEntity.getDataWatcher();
        npc.setAction(ActionsEnum.CROUCH);
        return new BukkitRunnable() {
            int count = 0;
            int index = 0;
            EntityPose pose;
            @Override
            public void run() {
                count += intervalInTicks;
                if(count > 100) {
                    updatePose(npcEntity, dataWatcher, EntityPose.STANDING);
                    npc.setAction(ActionsEnum.STANDING);
                    this.cancel();
                    return;
                }
                pose = index % 2 == 0 ? EntityPose.STANDING : EntityPose.CROUCHING;
                updatePose(npcEntity, dataWatcher, pose);
                index++;
            }
        };
    }

    private void updatePose(EntityPlayer npc, DataWatcher dataWatcher, EntityPose pose) {
        dataWatcher.set(DataWatcherRegistry.s.a(6), pose);
        for(Player player : Bukkit.getOnlinePlayers()) {
            Utils.sendPacket(player, new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher, true));
        }
    }

}
