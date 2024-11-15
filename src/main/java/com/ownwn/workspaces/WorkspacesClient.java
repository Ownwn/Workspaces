package com.ownwn.workspaces;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class WorkspacesClient {

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        System.out.println("client setup");
    }
}
