package io.alwa.featuredserver;

import net.minecraft.client.multiplayer.ServerData;

public class ServerDataTest extends ServerData
{
    public ServerDataTest(String name, String ip, boolean isLan)
    {
        super(name, ip, false);
    }
}
