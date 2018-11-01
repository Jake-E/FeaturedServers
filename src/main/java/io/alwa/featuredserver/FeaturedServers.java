package io.alwa.featuredserver;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Mod(modid = FeaturedServers.MODID, name = FeaturedServers.NAME, version = "@VERSION@", clientSideOnly = true)
public class FeaturedServers {
    public static final String MODID = "featuredservers";
    public static final String NAME = "FeaturedServers";

    private static Logger logger;
    private FileReader serversFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException {
        logger = event.getModLog();
        File configFolder = new File(event.getModConfigurationDirectory(), "FeaturedServers");
        if(!configFolder.exists()) configFolder.mkdirs();

        File serverList = new File(configFolder, "featuredservers.json");
        if (!serverList.exists()) {
            serverList.createNewFile();
            FileWriter writer = new FileWriter(serverList);
            writer.write("[\n" +
                    "  {\n" +
                    "    \"serverName\": \"Featured Server\",\n" +
                    "    \"serverIP\": \"127.0.0.1\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"serverName\": \"Another Server!\",\n" +
                    "    \"serverIP\": \"192.168.1.1\"\n" +
                    "  }\n" +
                    "]");
            writer.close();
        }

        serversFile = new FileReader(serverList.getPath());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(serversFile);
        ServerData[] featuredList = gson.fromJson(reader, ServerData[].class);

        ServerList serverList = new ServerList(Minecraft.getMinecraft());

        try {
            for (ServerData server : featuredList) {
                if (inList(server, serverList)) {
                    logger.log(Level.INFO, "Featured server already in server list");
                } else {
                    logger.log(Level.INFO, "Adding featured server");
                    serverList.addServerData(server);
                    serverList.saveServerList();
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Boolean inList(ServerData server, ServerList list) {
        if(list == null) return false;

        for (int i = 0; i < list.countServers(); i++) {
            ServerData serverData = list.getServerData(i);
            if(serverData.serverName != null && serverData.serverIP != null) {
                if (serverData.serverName.equalsIgnoreCase(server.serverName) && serverData.serverIP.equalsIgnoreCase(server.serverIP)) {
                    return true;
                }
            }
        }
        return false;
    }

}
