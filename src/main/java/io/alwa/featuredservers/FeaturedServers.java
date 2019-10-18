package io.alwa.featuredservers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;


@Mod("featuredservers")
public class FeaturedServers {
    private static final Logger LOGGER = LogManager.getLogger();
    private static String FMLConfigFolder;
    private FileReader serversFile;

    public FeaturedServers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent event) -> {
            try {
                doClientStuff(event);
            } catch (IOException e) {
                // Urgh, why is this so ugly...
            }
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
                    "    \"serverIP\": \"127.0.0.1\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"serverName\": \"Another Server!\",\n" +
                    "    \"serverIP\": \"192.168.1.1\"\n" +
                    "  }\n" +
                    "]");
            writer.close();
        }

        serversFile = new FileReader(featuredServerList.getPath());

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(serversFile);
        ServerDataHelper[] featuredList = gson.fromJson(reader, ServerDataHelper[].class);
        if (featuredList != null) {
            ServerList serverList = new ServerList(Minecraft.getInstance());
            for (ServerDataHelper serverhelp : featuredList) {
                ServerData server = new ServerData(serverhelp.serverName, serverhelp.serverIP, false);
                if (inList(server, serverList)) {
                    LOGGER.log(Level.INFO, "Featured server already in server list");
                } else {
                    LOGGER.log(Level.INFO, "Adding featured server");
                    serverList.addServerData(server);
                    serverList.saveServerList();
                }
            }
        }
    }

    void onConfigLoad(ModConfig.Loading event) {
        FMLConfigFolder = event.getConfig().getFullPath().getParent().toString();
    }

    public static Boolean inList(ServerData server, ServerList list) {
        if (list == null) return false;

        for (int i = 0; i < list.countServers(); i++) {
            ServerData serverData = list.getServerData(i);
            if (serverData.serverName != null && serverData.serverIP != null) {
                if (serverData.serverName.equalsIgnoreCase(server.serverName) && serverData.serverIP.equalsIgnoreCase(server.serverIP)) {
                    return true;
                }
            }
        }
        return false;
    }

    public class ServerDataHelper {

        public String serverName;
        public String serverIP;
    }

}
