package com.ownwn.workspaces.client;

import com.ownwn.workspaces.network.PacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Keybinds {

    public static KeyMapping[] keyMappings = new KeyMapping[9];

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        for (int i = 0; i < keyMappings.length; i++) {
            KeyMapping keyMapping = keyMappings[i];

            if (keyMapping.consumeClick()) {
                PacketHandler.sendWorkspacePacket(i);
            }
        }
    }
}
