package com.draxy.npc.manager;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static NPC getClosestNPC(Player player) {
        Optional<NPC> entityOptional = NPCManager.getInstance().getNPCs().stream().max((entity1, entity2) -> {
            double e1distance = entity1.getNPC().getBukkitEntity().getLocation().distanceSquared(player.getLocation());
            double e2distance = entity2.getNPC().getBukkitEntity().getLocation().distanceSquared(player.getLocation());
            return -Double.compare(e1distance, e2distance);
        });
        return entityOptional.orElse(null);
    }

    public static List<String> getNPCListSortedByDistance(Player player) {
        List<String> npcNames = new ArrayList<>();
        NPCManager.getInstance().getNPCs().stream().sorted((entity1, entity2) -> {
            double e1distance = entity1.getNPC().getBukkitEntity().getLocation().distanceSquared(player.getLocation());
            double e2distance = entity2.getNPC().getBukkitEntity().getLocation().distanceSquared(player.getLocation());
            return Double.compare(e1distance, e2distance);
        }).forEach(e -> npcNames.add(e.getName()));
        return npcNames;
    }

    public static <T extends PacketListener> void sendPacket(Player player, Packet<T> packet) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PlayerConnection connection = craftPlayer.getHandle().playerConnection;
        connection.sendPacket(packet);
    }

    public static List<String> getNPCListSortedByName() {
        List<String> npcNames = new ArrayList<>(NPCManager.getInstance().getNpcByName().keySet());
        Collections.sort(npcNames);
        return npcNames;
    }


    public static String color(String stringToBeColored) {
        return ChatColor.translateAlternateColorCodes('&', stringToBeColored);
    }

    public static String placeHolderReplacement(String replaced, NPC npc, CommandSender sender) {
        replaced = replaced.replaceAll("%npc%", npc.getName())
                .replaceAll("%action%", "" + npc.getAction().getActionName())
                .replaceAll("%locx%", "" + (int) npc.getNPC().getBukkitEntity().getLocation().getX())
                .replaceAll("%locy%", "" + (int) npc.getNPC().getBukkitEntity().getLocation().getY())
                .replaceAll("%locz%", "" + (int) npc.getNPC().getBukkitEntity().getLocation().getZ());
        if(sender instanceof Player) {
            Player player = (Player) sender;
            replaced = replaced.replaceAll("%player%", player.getName())
                .replaceAll("%distance%", "" + (int) npc.getNPC().getBukkitEntity().getLocation().distance(player.getLocation()));
        }
        return Utils.color(replaced);
    }



}
