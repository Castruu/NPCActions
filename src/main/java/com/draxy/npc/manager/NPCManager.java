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
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public final class NPCManager {
        private final static NPCManager instance = new NPCManager();

        private final List<NPC> NPC = new ArrayList<>();
        private final HashMap<String, NPC> npcByName = new HashMap<>();

        private NPCManager(){}

        public NPC createNPC(Player player, String nameInData) {
           Location location = player.getLocation();
           MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
           WorldServer nmsWorld = ((CraftWorld)Bukkit.getWorld("world")).getHandle(); // Change "world" to the world the NPC should be spawned in.
           GameProfile gameProfile = new GameProfile(UUID.randomUUID(), Utils.color(nameInData)); // Change "playername" to the name the NPC should have, max 16 characters.
           EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld)); // This will be the EntityPlayer (NPC) we send with the sendNPCPacket method.
           npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
           getSkin(player, nameInData, strings -> {
                saveNPC(nameInData, player, nameInData, nameInData, strings);
                gameProfile.getProperties().put("textures", new Property("textures", strings[0], strings[1]));
                addNPCPacket(npc);
           });
           NPC npcinstance = new NPC(npc, nameInData, ActionsEnum.STANDING);
           NPC.add(npcinstance);
           npcByName.put(nameInData, npcinstance);
           ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().players.removeIf(e -> e.getUniqueID().equals(npc.getUniqueID()));
           return npcinstance;
        }


        public void loadNPC(String nameInData, Location location, GameProfile profile, String[] npcInfo) {
            MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer nmsWorld = ((CraftWorld)Bukkit.getWorld("world")).getHandle();
            EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, profile, new PlayerInteractManager(nmsWorld));
            npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            Bukkit.getScheduler().runTaskLaterAsynchronously(NPCActions.getInstance(), () -> {
            profile.getProperties().put("textures", new Property("textures", npcInfo[0], npcInfo[1]));
            addNPCPacket(npc);
            }, 40);
            NPC npcinstance = new NPC(npc, nameInData, ActionsEnum.STANDING);
            NPC.add(npcinstance);
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

        private void getSkin(Player player, String name, Consumer<String[]> callback) {
            Bukkit.getScheduler().runTaskAsynchronously(NPCActions.getInstance(), () -> {
            String texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwMjIzMzkyMzM3MSwKICAicHJvZmlsZUlkIiA6ICIyYjVhZTczY2U5NTA0ZWY0OTBlYjkwNTUzZDYxM2M2NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJDYXN0cnV1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQwM2JkNGNhNjg4NGRkZTVjZmI0MGEzZTJhZjJiOWI1NmUwODNkYWFjMDFmYWIwZWUyOGJjN2IzYWQ1Yjc0ZDMiCiAgICB9CiAgfQp9";
            String signature = "FVms3Xcebl1Fm430vEt2sjEjY4FkUzaRUmDXnDZdhePsjZv4Y013vgYHxEmNux0mXfKWRYdMfKwMFBJ9M+hQ7H9u9h9FY0uDw+xzQcNT9dKR7NwXXlPpsFlnaDPi4r71zTMVXnTuvC8mpZ3v4af+NNZZimUKGYS7PACDrrAI2e+23SaOGWgXwL/nDmpyLupG83qS/uY/dYeRvAE6dNVSae+YsByR2AdLyRfGFE/6g18AyzC7WFMeMsj42mYyCXec0sj/JWX6Ld5ooZ7oG2VcxDi3L9gaoZ3X2aqx3iGJRqnsstxdf0OHAb6+kZJUAL1Jfam3g/cZqwVhGt1Dkvgsc3GuCQXlPEjhwc754wSh20i749ZKZJAMSAj+xiY6Tb7AtztuEfp1mUa5+JxSOGIzRfE/0t8JybiODddzCB+0CAI5cE1lsEIIgLMDY4vAhb4h4FuCIrhxNnmMZIOKy87tGyIA1VAj3ZuGun0aa65R3mDb1xofPK5dkOrYoHQtda8L8pzMo4iACxO5gHls/4vZfEDWNciA/VqTji6zquDTNpb34515KkgrTkHkOBxhHLmsmmyZ/kvCY+OfSuWZ6annVJM0k69dm/v0YlphAUFXrIhCaeRbsBGSZ3ZX0zTB8h6npuRaAx7+N8tWMT+D3V4zjpEqHWKRtATlcPINV94djMQ=";
            try{
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                InputStreamReader reader = new InputStreamReader(url.openStream());
                String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
                URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                        + "?unsigned=false");
                InputStreamReader reader2  = new InputStreamReader(url2.openStream());
                JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties")
                        .getAsJsonArray().get(0).getAsJsonObject();
                texture = property.get("value").getAsString();
                signature = property.get("signature").getAsString();
            } catch (IOException | IllegalStateException e) {
                EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                GameProfile profile = entityPlayer.getProfile();
                Property property = profile.getProperties().get("textures").iterator().next();
                texture = property.getValue();
                signature = property.getSignature();
            }
                callback.accept(new String[] {texture, signature});
            });
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
            Bukkit.getScheduler().runTaskAsynchronously(NPCActions.getInstance(), () -> {
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
            });
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
                , 15);
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
