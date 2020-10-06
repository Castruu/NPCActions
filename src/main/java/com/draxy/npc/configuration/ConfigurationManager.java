package com.draxy.npc.configuration;

import com.draxy.npc.NPCActions;

public class ConfigurationManager {


    private final static ConfigurationManager instance = new ConfigurationManager();

    public String STARTACTION,
    NPCINFORMATION,
    NPCPLAYERINFORMATION,
    CREATENPC,
    DELETENPC;

    public void setMessages() {
        STARTACTION = NPCActions.getInstance().getConfig().getString("messages.startaction");
        NPCINFORMATION = NPCActions.getInstance().getConfig().getString("messages.npcinfoasconsole");
        NPCPLAYERINFORMATION = NPCActions.getInstance().getConfig().getString("messages.npcinfoasplayer");
        CREATENPC = NPCActions.getInstance().getConfig().getString("messages.createnpc");
        DELETENPC = NPCActions.getInstance().getConfig().getString("messages.deletenpc");
    }







    public static ConfigurationManager getInstance() {
        return instance;
    }
}
