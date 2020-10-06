package com.draxy.npc.manager;

import com.draxy.npc.NPCActions;
import com.draxy.npc.actions.ActionsEnum;
import com.draxy.npc.configuration.DataManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class NPCManager {
        private final static NPCManager instance = new NPCManager();

        private final List<NPC> NPC = new ArrayList<>();
        private final HashMap<String, NPC> npcByName = new HashMap<>();

        private NPCManager(){}

        public NPC createNPC(Player player, String nameInData) {
            Location location = player.getLocation();
            MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer nmsWorld = ((CraftWorld)Bukkit.getWorld("world")).getHandle(); // Change "world" to the world the NPC should be spawned in.
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', nameInData)); // Change "playername" to the name the NPC should have, max 16 characters.
            EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld)); // This will be the EntityPlayer (NPC) we send with the sendNPCPacket method.
            npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            String[] skinInformation = getSkin(player, nameInData);
            gameProfile.getProperties().put("textures", new Property("textures", skinInformation[0], skinInformation[1]));
            addNPCPacket(npc);
            NPC npcinstance = new NPC(npc, nameInData, ActionsEnum.STANDING);
            NPC.add(npcinstance);
            npcByName.put(nameInData, npcinstance);
            saveNPC(nameInData, player, nameInData, nameInData, skinInformation);
            ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().players.removeIf(e -> e.getUniqueID().equals(npc.getUniqueID()));
            return npcinstance;
        }


        public void loadNPC(String nameInData, Location location, GameProfile profile) {
            MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer nmsWorld = ((CraftWorld)Bukkit.getWorld("world")).getHandle();
            GameProfile gameProfile = profile;
            EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
            npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            addNPCPacket(npc);
            NPC npcinstance = new NPC(npc, nameInData, ActionsEnum.STANDING);
            NPC.add(npcinstance);
            //currentAction.put(npc, ActionsEnum.STANDING);
            npcByName.put(nameInData, npcinstance);
            ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().players.removeIf(e -> e.getUniqueID().equals(npc.getUniqueID()));
        }

        public NPC deleteNPC(String npcName) {
            DataManager.getInstance().getConfig().set("npcs." + npcName, null);
            DataManager.getInstance().saveConfig();
            NPC npc = npcByName.get(npcName);
            for(Player player : Bukkit.getOnlinePlayers()) {
                Utils.sendPacket(player, new PacketPlayOutEntityDestroy(npc.getNPC().getId()));
            }
            npcByName.remove(npcName);
            NPC.remove(npc);
            return npc;
        }

        private String[] getSkin(Player player, String name) {
            try{
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                InputStreamReader reader = new InputStreamReader(url.openStream());
                String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
                URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                        + "?unsigned=false");
                InputStreamReader reader2  = new InputStreamReader(url2.openStream());
                JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties")
                        .getAsJsonArray().get(0).getAsJsonObject();
                String texture = property.get("value").getAsString();
                String signature = property.get("signature").getAsString();
                return new String[] {texture, signature};
            } catch (IOException e) {
                EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                GameProfile profile = entityPlayer.getProfile();
                Property property = profile.getProperties().get("textures").iterator().next();
                String texture = property.getValue();
                String signature = property.getSignature();
                return new String[] {texture, signature};
            }
        }

        public void addNPCPacket(EntityPlayer npc) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                npcPacket(player, npc);
            }
        }

        public void addJoinPacket(Player player) {
            for (NPC npc : NPC) {
                npcPacket(player, npc.getNPC());
            }
        }

        private void saveNPC(String nameInData, Player player, String npcDisplayName, String skinFromPlayer, String[] skinInformation) {
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".x", (int) player.getLocation().getX());
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".y", (int) player.getLocation().getY());
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".z", (int) player.getLocation().getZ());
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".p", player.getLocation().getPitch());
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".yaw", player.getLocation().getYaw());
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".world", player.getLocation().getWorld().getName());
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".name", skinFromPlayer);
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".npcname", npcDisplayName);
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".text", skinInformation[0]);
            DataManager.getInstance().getConfig().set("npcs." + nameInData + ".signature", skinInformation[1]);
            DataManager.getInstance().saveConfig();
        }

        private void npcPacket(Player player, EntityPlayer npc) {
        DataWatcher dataWatcher = npc.getDataWatcher();
        dataWatcher.set(DataWatcherRegistry.a.a(16), (byte)127);
        Utils.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        Utils.sendPacket(player, new PacketPlayOutNamedEntitySpawn(npc));
        Utils.sendPacket(player, new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256/360)));
        Utils.sendPacket(player, new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher, true));
        Bukkit.getScheduler().runTaskLater(NPCActions.getInstance(), () ->
                        Utils.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc))
                , 5);
         }

        public List<NPC> getNPCs() {
            return NPC;
        }


        public HashMap<String, NPC> getNpcByName() {
            return npcByName;
        }

        public static NPCManager getInstance() {
        return instance;
         }

}
