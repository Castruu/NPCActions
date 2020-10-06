package com.draxy.npc;

import com.draxy.npc.commands.*;
import com.draxy.npc.commands.actions.CrouchCommand;
import com.draxy.npc.commands.actions.JumpCommand;
import com.draxy.npc.commands.actions.SwingHandCommand;
import com.draxy.npc.configuration.ConfigurationManager;
import com.draxy.npc.configuration.DataManager;
import com.draxy.npc.events.OnJoin;
import com.draxy.npc.manager.NPC;
import com.draxy.npc.manager.NPCManager;
import com.draxy.npc.manager.Utils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class NPCActions extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        DataManager.getInstance().saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new OnJoin(), this);
        setupServer();
        ConfigurationManager.getInstance().setMessages();
    }

    @Override
    public void onDisable() {
        removeNPCs();
    }

    private void setupServer() {
        getCommand("crouch").setExecutor(new CrouchCommand());
        getCommand("attack").setExecutor(new SwingHandCommand());
        getCommand("jump").setExecutor(new JumpCommand());
        getCommand("createnpc").setExecutor(new NPCCreation());
        getCommand("listnpcs").setExecutor(new NPCList());
        getCommand("deletenpc").setExecutor(new NPCDelete());
        getCommand("npcinfo").setExecutor(new NPCInfo());
        getCommand("reloadnpcs").setExecutor(new ConfigurationReload());
        loadNPCs();
    }

    public void loadNPCs() {
        FileConfiguration config = DataManager.getInstance().getConfig();
        if(config.contains("npcs"))
        for(String key : config.getConfigurationSection("npcs").getKeys(false)) {
            Location location = new Location(Bukkit.getWorld(config.getString("npcs." + key + ".world")),
                    config.getInt("npcs." + key + ".x"), config.getInt("npcs." + key + ".y"), config.getInt("npcs." + key + ".z"));
            location.setPitch((float) config.getDouble("npcs." + key + ".p"));
            location.setYaw((float) config.getDouble("npcs." + key + ".yaw"));
            String npcname = config.getString("npcs."  + key +  ".npcname");
            GameProfile profile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', npcname));
            profile.getProperties().put("textures", new Property("textures", config.getString("npcs."  + key +  ".text"),
                    config.getString("npcs." + key +  ".signature")));
            NPCManager.getInstance().loadNPC(key, location, profile);
        }
    }

    public void removeNPCs() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(NPC npc : NPCManager.getInstance().getNPCs()) {
                Utils.sendPacket(player, new PacketPlayOutEntityDestroy(npc.getNPC().getId()));
            }
        }
        NPCManager.getInstance().getNPCs().clear();
        NPCManager.getInstance().getNpcByName().clear();
    }

    public static NPCActions getInstance() {
        return NPCActions.getPlugin(NPCActions.class);
    }
}

