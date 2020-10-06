package com.draxy.npc.commands;

import com.draxy.npc.NPCActions;
import com.draxy.npc.configuration.ConfigurationManager;
import com.draxy.npc.configuration.DataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ConfigurationReload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        NPCActions.getInstance().reloadConfig();
        DataManager.getInstance().reloadConfig();
        NPCActions.getInstance().removeNPCs();
        NPCActions.getInstance().loadNPCs();
        ConfigurationManager.getInstance().setMessages();
        return false;
    }
}
