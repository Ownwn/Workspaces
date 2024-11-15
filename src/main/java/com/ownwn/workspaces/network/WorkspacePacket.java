package com.ownwn.workspaces.network;

import com.ownwn.workspaces.Workspaces;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WorkspacePacket {
    private final int workspace;
    public WorkspacePacket(int workspace) {
        this.workspace = workspace;
    }

    public WorkspacePacket(FriendlyByteBuf buf) {
        this.workspace = buf.readInt();
    }

    public static void encode(WorkspacePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.workspace);
    }

    public static void handle(WorkspacePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        ctxSupplier.get().enqueueWork(() -> {
            Workspaces.teleportPlayer(ctxSupplier.get().getSender(), packet.workspace);
        });

        ctxSupplier.get().setPacketHandled(true);
    }
}
