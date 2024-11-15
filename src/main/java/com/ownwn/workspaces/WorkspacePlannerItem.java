package com.ownwn.workspaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class WorkspacePlannerItem extends Item {
    public WorkspacePlannerItem(Properties properties) {
        super(properties);
    }

    public static WorkspacePlannerItem createItem() {
        return new WorkspacePlannerItem(
                new Item.Properties()
                        .stacksTo(1)
                        .rarity(Rarity.UNCOMMON)

        );
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player user, @NotNull InteractionHand hand) {
        if (level.isClientSide()) {
            return super.use(level, user, hand);
        }

        ItemStack stack = user.getItemInHand(hand);
        if (stack == null) {
            return super.use(level, user, hand);
        }

        int workspaceNum = stack.getOrCreateTag().getInt("workspaceNum");

        if (user.isCrouching()) {

            ListTag locations = (ListTag) stack.getOrCreateTag().get("workspaces");

            CompoundTag location = locations.getCompound(workspaceNum);

            location.putDouble("x", user.position().x);
            location.putDouble("y", user.position().y);
            location.putDouble("z", user.position().z);

            locations.set(workspaceNum, location);


//            locations.getCompound(0).getDouble("x");

        } else {
            stack.getOrCreateTag().putInt("workspaceNum", (workspaceNum + 1) % 9);
        }

        return InteractionResultHolder.success(stack);

    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Component oldName = super.getName(stack);

        int workspaceNum = stack.getOrCreateTag().getInt("workspaceNum");

        return oldName.copy().append(Component.literal(" (" + (workspaceNum + 1) + ")"));
    }

    private void setDefaultWorkspaces(CompoundTag tag) {

        ListTag list = new ListTag();
        for (int i = 0; i < 9; i++) {
            CompoundTag vec = new CompoundTag();
            vec.putDouble("x", 0);
            vec.putDouble("y", 0);
            vec.putDouble("z", 0);
            list.add(vec);
        }

        tag.put("workspaces", list);

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag type) {
        CompoundTag tag = stack.getOrCreateTag();

        if (tag.get("workspaces") == null) {
            setDefaultWorkspaces(tag);
        }

        ListTag locations = (ListTag) tag.get("workspaces");

        if (locations == null) {
            System.out.println("null");
            return;
        }

        for (int i = 0; i < locations.size(); i++) {
            CompoundTag pos = locations.getCompound(i);

            String posString;
            double x = pos.getDouble("x");
            double y = pos.getDouble("y");
            double z = pos.getDouble("z");
            if (x == 0 && y == 0 && z == 0) {
                posString = "Not set!";
            } else {
                posString = (int) x + " " + (int) y + " " + (int) z;
            }

            components.add(Component.literal("Workspace " + (i + 1) + ": " + posString));
        }
    }

}
