package com.draxy.npc.actions;

import com.draxy.npc.NPCActions;
import com.draxy.npc.manager.NPC;
import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.EnumHand;
import net.minecraft.server.v1_16_R2.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class SwingHandAction extends Actions {

    private final int action;
    private final long intervalInTicks;

    public SwingHandAction(long intervalInMS, EnumHand hand) {
        this.action = (hand == EnumHand.MAIN_HAND) ? 0 : 3;
        double timeInSeconds = intervalInMS/1000.0;
        double toCeil = timeInSeconds *20.0;
        this.intervalInTicks = (long) Math.ceil(toCeil);
    }

    @Override
    public void action(NPC npc) {
        getRunnable(npc).runTaskTimerAsynchronously(NPCActions.getInstance(), 0, intervalInTicks);
    }

    @Override
    protected BukkitRunnable getRunnable(NPC npc) {
        PacketPlayOutAnimation npcAnimation = new PacketPlayOutAnimation(npc.getNPC(), action);
        npc.setAction(ActionsEnum.ATTACKING);
        return new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                counter+=intervalInTicks;
                if(counter > 100) {
                    npc.setAction(ActionsEnum.STANDING);
                    this.cancel();
                    return;
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Utils.sendPacket(player, npcAnimation);
                }
            }
        };
    }

}
