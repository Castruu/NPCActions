package com.draxy.npc.commands;

import com.draxy.npc.configuration.ConfigurationManager;
import com.draxy.npc.manager.NPC;
import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCInfo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            sender.sendMessage(Utils.color("&cFollow the correct usage -> /npcinfo <name>"));
            return true;
        }
        String name = args[0];
        if(!NPCManager.getInstance().getNpcByName().containsKey(name)) {
            sender.sendMessage(Utils.color("&cThis NPC does not exist!"));
            return true;
        }
        NPC npc = NPCManager.getInstance().getNpcByName().get(name);
        if(sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(Utils.placeHolderReplacement(ConfigurationManager.getInstance().NPCPLAYERINFORMATION, npc, player));
            return true;
        }
        sender.sendMessage(Utils.placeHolderReplacement(ConfigurationManager.getInstance().NPCINFORMATION, npc, sender));
        return true;
    }

}
