package com.draxy.npc.commands;

import com.draxy.npc.configuration.ConfigurationManager;
import com.draxy.npc.manager.NPC;
import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NPCDelete  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            sender.sendMessage(Utils.color("&cFollow the correct usage of the command -> /deletenpc <name>"));
            return true;
        }
        if(!NPCManager.getInstance().getNpcByName().containsKey(args[0])) {
            sender.sendMessage(Utils.color("&cThis NPC does not exist!"));
            return true;
        }
        NPC npc = NPCManager.getInstance().deleteNPC(args[0]);
        sender.sendMessage(Utils.placeHolderReplacement(ConfigurationManager.getInstance().DELETENPC, npc, sender));
        return true;
    }
}
