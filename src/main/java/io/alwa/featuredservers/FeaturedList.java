package io.alwa.featuredservers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class FeaturedList {

    public static final Map<String, FeaturedServerData> servers = new HashMap<>();

    public void doFeaturedListStuff(FeaturedServers.ServerDataHelper[] featuredList) {
        if (featuredList != null) {
            ServerList serverList = new ServerList(Minecraft.getInstance());
            for (FeaturedServers.ServerDataHelper serverhelp : featuredList) {
                FeaturedServerData server = new FeaturedServerData(serverhelp.serverName, serverhelp.serverIP, false, serverhelp.disableButtons);
                if(serverhelp.forceResourcePack != null && serverhelp.forceResourcePack) server.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                if (inList(server, serverList)) {
                    FeaturedServers.LOGGER.log(Level.INFO, "Featured server already in server list");
                } else {
                    FeaturedServers.LOGGER.log(Level.INFO, "Adding featured server");
                    serverList.add(server);
                    serverList.save();
                }
                servers.put(server.ip, server);
            }
        }
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

    public static class FeaturedServerData extends ServerData {

        public final boolean disableButtons;

        public FeaturedServerData(String name, String ip, boolean forceResourcePack, Boolean disableButtons) {
            super(name, ip, forceResourcePack);
            this.disableButtons = disableButtons != null && disableButtons;
        }
    }

}
