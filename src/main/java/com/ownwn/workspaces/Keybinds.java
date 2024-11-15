package com.ownwn.workspaces;

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

        for (KeyMapping keyMapping : keyMappings) {
            if (keyMapping.consumeClick()) {
                System.out.println("pressed!" + keyMapping.getName());
            }
        }
    }
}
