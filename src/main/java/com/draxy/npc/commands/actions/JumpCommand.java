package com.draxy.npc.commands.actions;

import com.draxy.npc.actions.ActionsEnum;
import com.draxy.npc.actions.JumpAction;
import com.draxy.npc.configuration.ConfigurationManager;
import com.draxy.npc.manager.NPC;
import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JumpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cThis command can only be performed by a player!"));
            return true;
        }
        Player player = (Player) sender;
        NPC npc = Utils.getClosestNPC(player);
        if(npc == null) {
            sender.sendMessage(Utils.color("&cNo NPC was found. Make sure the NPC list is not empty."));
            return true;
        }
        if(npc.getAction() != ActionsEnum.STANDING) {
            sender.sendMessage(Utils.color("&cThis NPC is currently executing a Task."));
            return true;
        }
        JumpAction.getInstance().action(npc);
        player.sendMessage(Utils.placeHolderReplacement(ConfigurationManager.getInstance().STARTACTION, npc, player));
        return true;
    }

}
