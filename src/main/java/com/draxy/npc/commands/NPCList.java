package com.draxy.npc.commands;

import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class NPCList  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(NPCManager.getInstance().getNPCs().isEmpty()) {
            sender.sendMessage(Utils.color("&cThere is no NPC Created!"));
            return true;
        }
        StringBuilder stringBuilder = new StringBuilder(ChatColor.BLUE + "NPCs:\n");
        if(sender instanceof ConsoleCommandSender) {
            Utils.getNPCListSortedByName().forEach(name -> stringBuilder.append("&1-&b").append(name).append("\n"));
            sender.sendMessage(Utils.color(stringBuilder.toString()));
            return true;
        }
        else if(sender instanceof Player) {
            Player player = (Player) sender;
            Utils.getNPCListSortedByDistance(player).forEach(name -> stringBuilder.append("&1-&b").append(name).append("\n"));
            player.sendMessage(Utils.color(stringBuilder.toString()));
            return true;
        }
        return true;
    }
}
