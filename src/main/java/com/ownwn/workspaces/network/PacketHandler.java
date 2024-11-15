package com.ownwn.workspaces.network;

import com.ownwn.workspaces.Workspaces;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;


// lots taken from https://github.com/OfekN-mods/crafting-on-a-stick/blob/1.20.0/src/main/java/com/ofek2608/crafting_on_a_stick/network/COASPacketHandler.java
// since the forge docs are meh
public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static int ID = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Workspaces.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void load() {

        Optional<NetworkDirection> serverBound = Optional.of(NetworkDirection.PLAY_TO_SERVER);

        INSTANCE.registerMessage(++ID, WorkspacePacket.class, WorkspacePacket::encode, WorkspacePacket::new, WorkspacePacket::handle, serverBound);
    }

    public static void sendWorkspacePacket(int workspace) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), new WorkspacePacket(workspace));
    }
}
