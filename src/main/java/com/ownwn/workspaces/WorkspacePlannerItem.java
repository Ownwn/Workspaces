package com.ownwn.workspaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import java.awt.*;
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
        if (!(stack.getItem() instanceof WorkspacePlannerItem)) {
            return super.use(level, user, hand);
        }

        CompoundTag tag = stack.getOrCreateTag();

        int workspaceNum = tag.getInt("workspaceNum");

        if (level.dimension() != Level.OVERWORLD) {
            Workspaces.sendFailMessage(user, "workspaces.workspace_planner.use_error");
            return InteractionResultHolder.fail(stack);
        }

        if (user.isCrouching()) {

            CompoundTag workspace = getWorkspace(tag, workspaceNum);

            ListTag workspaces = getWorkspaceList(tag);


            updateWorkspaceCoords(workspace, user.position().x, user.position().y, user.position().z);
            updateWorkspaceRotation(workspace, user.getXRot(), user.getYRot());

            workspaces.set(workspaceNum, workspace);

        } else {
            tag.putInt("workspaceNum", (workspaceNum + 1) % 9);
        }

        return InteractionResultHolder.success(stack);

    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Component oldName = super.getName(stack);

        int workspaceNum = stack.getOrCreateTag().getInt("workspaceNum");

        return oldName.copy().append(Component.literal(" (" + (workspaceNum + 1) + ")"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag type) {
        CompoundTag tag = stack.getOrCreateTag();

        if (!hasValidCoords(tag)) {
            setDefaultWorkspaces(tag);
        }

        ListTag locations = getWorkspaceList(tag);

        if (locations == null) {
            components.add(Component.literal("Error").withStyle(Style.EMPTY.withColor(Color.red.getRGB())));
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

    public static CompoundTag getWorkspace(CompoundTag itemTag, int workspaceNum) {
        if (!hasValidCoords(itemTag)) {
            setDefaultWorkspaces(itemTag);
        }

        ListTag workspaces = getWorkspaceList(itemTag);

        return workspaces.getCompound(workspaceNum);
    }

    public static ListTag getWorkspaceList(CompoundTag tag) {
        return (ListTag) tag.get("workspaces");
    }

    private void updateWorkspaceCoords(CompoundTag tag, double x, double y, double z) {
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);
    }

    private void updateWorkspaceRotation(CompoundTag tag, float pitch, float yaw) {
        tag.putFloat("pitch", pitch);
        tag.putFloat("yaw", yaw);
    }

    public static boolean hasValidCoords(CompoundTag tag) {
        return (tag.get("workspaces") != null);
    }

    public static void setDefaultWorkspaces(CompoundTag tag) {
        ListTag workspaceList = new ListTag();

        for (int i = 0; i < 9; i++) {
            CompoundTag workspace = new CompoundTag();
            workspace.putDouble("x", 0);
            workspace.putDouble("y", 0);
            workspace.putDouble("z", 0);

            workspace.putFloat("pitch", 0);
            workspace.putFloat("yaw", 0);


            workspaceList.add(workspace);
        }

        tag.put("workspaces", workspaceList);
    }
}
