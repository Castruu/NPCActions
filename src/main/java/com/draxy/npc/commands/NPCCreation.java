package com.draxy.npc.commands;

import com.draxy.npc.configuration.ConfigurationManager;
import com.draxy.npc.manager.NPC;
import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCreation  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cThis command can only be performed by a player!"));
            return true;
        }
        if(args.length != 1) {
            sender.sendMessage(Utils.color("&cFollow the correct usage! /createnpc <name>"));
            return true;
        }
        Player player = (Player) sender;
        if(NPCManager.getInstance().getNpcByName().containsKey(args[0])) {
            sender.sendMessage(Utils.color("&cThis NPC already exists!"));
            return true;
        }
        NPC npc = NPCManager.getInstance().createNPC(player, args[0]);
        sender.sendMessage(Utils.placeHolderReplacement(ConfigurationManager.getInstance().CREATENPC, npc, player));

        return true;
    }
}
