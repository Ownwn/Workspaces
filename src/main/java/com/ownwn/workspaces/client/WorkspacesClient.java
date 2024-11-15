package com.ownwn.workspaces.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.ownwn.workspaces.Workspaces;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class WorkspacesClient {



    @SubscribeEvent
    public void onClientSetup(RegisterKeyMappingsEvent event) {

        for (int i = 0; i < 9; i++) {

            KeyMapping keyMapping = new KeyMapping(
                    "key." + Workspaces.MODID + ".workspace" + (i+1),
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_1 + i,  // slight hack, KEY_2 is just KEY_1 + 1 etc
                    "key.categories." + Workspaces.MODID
            );

            Keybinds.keyMappings[i] = keyMapping;
            event.register(keyMapping);
        }
    }
}
