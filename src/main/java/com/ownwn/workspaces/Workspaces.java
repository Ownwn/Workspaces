package com.ownwn.workspaces;

import com.ownwn.workspaces.client.Keybinds;
import com.ownwn.workspaces.client.WorkspacesClient;
import com.ownwn.workspaces.network.PacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;
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

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(new Keybinds());

            eventBus.register(new WorkspacesClient());
        });


        PacketHandler.load();

        ITEMS.register(eventBus);

    }
    public static void teleportPlayer(ServerPlayer player, int workspaceNum) {
        if (player == null) {
            return;
        }

        Optional<ItemStack> item = player.getInventory().items.stream()
                .filter(
                        stack -> stack != null && stack.getItem() instanceof WorkspacePlannerItem
                ).findFirst();
        if (item.isEmpty() || !item.get().hasTag()) {
            return;
        }

        ItemStack stack = item.get();
        CompoundTag tag = stack.getTag();


        CompoundTag workspace = WorkspacePlannerItem.getWorkspace(tag, workspaceNum);

        double x = workspace.getDouble("x");
        double y = workspace.getDouble("y");
        double z = workspace.getDouble("z");

        float pitch = workspace.getFloat("pitch");
        float yaw = workspace.getFloat("yaw");

        if (x == 0 && y == 0 && z == 0) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (player.level().dimension() != Level.OVERWORLD) {
            sendFailMessage(player, "You must be in the overworld to teleport!");
            return;
        }

        // make sure chunk is loaded, hopefully prevent "moved too quickly!" problems
        level.getChunkAt(BlockPos.containing(x, y, z));

        // why the hell is it yaw then pitch?
        player.teleportTo(level, x, y, z, yaw, pitch);
    }

    public static void sendFailMessage(Player player, String message) {
        player.displayClientMessage(Component.literal(message)
                .withStyle(Style.EMPTY.withColor(
                        ChatFormatting.RED
                )), true);
    }
}
