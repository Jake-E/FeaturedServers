package io.alwa.featuredservers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod("featuredservers")
public class FeaturedServers {
    private static final Logger LOGGER = LogManager.getLogger();
    private static String FMLConfigFolder;
    private static ServerList serverList;
    public static final Map<String, ServerData> servers = new HashMap<>();

    public FeaturedServers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent event) -> {
            try {
                doClientStuff(event);
            } catch (IOException ignored) {}
        });

    }

    private void doClientStuff(final FMLClientSetupEvent event) throws IOException {
        File configFolder = new File(FMLConfigFolder, "FeaturedServers");
        if (!configFolder.exists()) configFolder.mkdirs();

        File featuredServerList = new File(configFolder, "featuredservers.json");
        if (!featuredServerList.exists()) {
            featuredServerList.createNewFile();
            FileWriter writer = new FileWriter(featuredServerList);
            writer.write("[\n" +
                    "  {\n" +
                    "    \"serverName\": \"Featured Server\",\n" +
                    "    \"serverIP\": \"127.0.0.1\",\n" +
                    "    \"forceResourcePack\": \"true\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"serverName\": \"Another Server!\",\n" +
                    "    \"serverIP\": \"192.168.1.1\",\n" +
                    "    \"forceResourcePack\": \"false\"\n" +
                    "  }\n" +
                    "]");
            writer.close();
        }

        FileReader serversFile = new FileReader(featuredServerList.getPath());

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(serversFile);
        ServerDataHelper[] featuredList = gson.fromJson(reader, ServerDataHelper[].class);
        if (featuredList != null) {
            serverList = new ServerList(Minecraft.getInstance());
            for (ServerDataHelper serverhelp : featuredList) {
                ServerData server = new ServerData(serverhelp.serverName, serverhelp.serverIP, false);
                if(serverhelp.forceResourcePack != null && serverhelp.forceResourcePack) server.setResourcePackStatus(ServerData.ServerResourceMode.ENABLED);
                if (inList(server, serverList)) {
                    LOGGER.log(Level.INFO, "Featured server already in server list");
                } else {
                    LOGGER.log(Level.INFO, "Adding featured server");
                    serverList.add(server);
                    serverList.save();
                }
                servers.put(server.ip, server);
            }
        }
    }

    void onConfigLoad(ModConfig.Loading event) {
        FMLConfigFolder = event.getConfig().getFullPath().getParent().toString();
    }

    public static Boolean inList(ServerData server, ServerList list) {
        return list != null && toList(list).stream().anyMatch(serverData -> serverData.name != null && serverData.ip != null
                && serverData.name.equalsIgnoreCase(server.name) && serverData.ip.equalsIgnoreCase(server.ip));
    }

    public static List<ServerData> toList(ServerList list) {
        List<ServerData> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            data.add(list.get(i));
        }
        return data;
    }

    public static ServerList getServerList() {
        return serverList;
    }

    public class ServerDataHelper {

        public String serverName;
        public String serverIP;
        public Boolean forceResourcePack;
    }

}
