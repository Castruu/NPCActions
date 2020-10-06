package com.draxy.npc.configuration;

import com.draxy.npc.NPCActions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {


    public static DataManager instance = new DataManager();
    private FileConfiguration dataConfig;
    private File configFile;

    public void reloadConfig() {
        if(this.configFile == null) this.configFile = new File(NPCActions.getInstance().getDataFolder(), "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = NPCActions.getInstance().getResource("data.yml");
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig(){
        if(this.dataConfig == null) reloadConfig();
        return this.dataConfig;
    }


    public void saveConfig(){
        if(this.dataConfig == null || this.configFile == null) return;
        try{
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            NPCActions.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
            e.printStackTrace();
        }
    }


    public void saveDefaultConfig() {
        if(this.configFile == null) this.configFile = new File(NPCActions.getInstance().getDataFolder(), "data.yml");
        if(!this.configFile.exists()) NPCActions.getInstance().saveResource("data.yml", false);
    }

    public static DataManager getInstance() {
        return instance;
    }

}
