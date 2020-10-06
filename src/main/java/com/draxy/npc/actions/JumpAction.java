package com.draxy.npc.actions;

import com.draxy.npc.NPCActions;
import com.draxy.npc.manager.NPC;
import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class JumpAction extends Actions {

    private static final JumpAction instance = new JumpAction();

    @Override
    public void action(NPC npc) {
         getRunnable(npc).runTaskTimerAsynchronously(NPCActions.getInstance(), 0, 20);
    }

    @Override
    protected BukkitRunnable getRunnable(NPC npc) {
        final PacketPlayOutEntity.PacketPlayOutRelEntityMove jumpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(npc.getNPC().getId(), (short) 0, (short) 3000, (short) 0, false);
        final PacketPlayOutEntity.PacketPlayOutRelEntityMove fallPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(npc.getNPC().getId(), (short) 0, (short) -3000, (short) 0, false);
        npc.setAction(ActionsEnum.JUMP);
        return new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if(i > 4) {
                    npc.setAction(ActionsEnum.STANDING);
                    this.cancel();
                    return;
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Utils.sendPacket(player, jumpPacket);
                    Bukkit.getScheduler().runTaskLater(NPCActions.getInstance(),
                            () -> Utils.sendPacket(player, fallPacket), 7);
                }
                i++;
             }
        };
    }

    public static JumpAction getInstance() {
        return instance;
    }

}
