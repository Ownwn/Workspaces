package com.ownwn.workspaces;

import com.ownwn.workspaces.client.Keybinds;
import com.ownwn.workspaces.client.WorkspacesClient;
import com.ownwn.workspaces.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Workspaces.MODID)
public class Workspaces
{
    public static final String MODID = "workspaces";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> WORKSPACE_PLANNER_ITEM = ITEMS.register("workspace_planner", WorkspacePlannerItem::createItem);


    public Workspaces(FMLJavaModLoadingContext context) {

        IEventBus eventBus = context.getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Keybinds());

        eventBus.register(new WorkspacesClient());

        PacketHandler.load();

        ITEMS.register(eventBus);

    }

    public static void teleportPlayer(ServerPlayer player, int workspace) {
        if (player == null || player.getInventory().items == null) {
            return;
        }

        Optional<ItemStack> item = player.getInventory().items.stream()
                .filter(
                        stack -> stack != null && stack.getItem() instanceof WorkspacePlannerItem
                ).findFirst();
        if (item.isEmpty()) {
            return;
        }

        ItemStack stack = item.get();
    }
}
