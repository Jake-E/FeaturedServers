package io.alwa.featuredservers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Mod("featuredservers")
public class FeaturedServers {
    public static final Logger LOGGER = LogManager.getLogger();
    private static String FMLConfigFolder;

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
            writer.write("""
                    [
                      {
                        "serverName": "Featured Server",
                        "serverIP": "127.0.0.1",
                        "forceResourcePack": "true",
                        "disableButtons": "true"
                      },
                      {
                        "serverName": "Another Server!",
                        "serverIP": "192.168.1.1",
                        "forceResourcePack": "false",
                        "disableButtons": "false"
                      }
                    ]""");
            writer.close();
        }

        FileReader serversFile = new FileReader(featuredServerList.getPath());

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(serversFile);
        ServerDataHelper[] featuredList = gson.fromJson(reader, ServerDataHelper[].class);
        new FeaturedList().doFeaturedListStuff(featuredList);
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        FMLConfigFolder = event.getConfig().getFullPath().getParent().toString();
    }

    public static class ServerDataHelper {

        public String serverName;
        public String serverIP;
        public Boolean forceResourcePack;
        public Boolean disableButtons;
    }
}
