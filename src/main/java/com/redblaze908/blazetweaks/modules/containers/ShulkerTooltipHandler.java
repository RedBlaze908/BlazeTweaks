package com.redblaze908.blazetweaks.modules.containers;

// Tooltip library
import net.minecraft.item.ItemShulkerBox;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// Write in chat library
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import javafx.scene.control.TextFormatter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ShulkerTooltipHandler {

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {

        if (event.getItemStack().getItem() instanceof ItemShulkerBox) {
            ItemStack stack = event.getItemStack();
            int sum = 0;
            double filledSlotsRatio = 0.0;

            if (stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();

                if (nbt.hasKey("BlockEntityTag", 10) == true) {
                    NBTTagCompound blockEntityTag = nbt.getCompoundTag("BlockEntityTag");
                    NBTTagList itemsList = blockEntityTag.getTagList("Items", 10);

                    if (blockEntityTag != null && itemsList != null) {

                        InventoryBasic inv = new InventoryBasic("Shulker", false, 27);

                        for (int i = 0; i < itemsList.tagCount(); ++i) {
                            NBTTagCompound itemTag = itemsList.getCompoundTagAt(i);

                            int slotIndex = itemTag.getByte("Slot");
                            ItemStack stackInSlot = new ItemStack(itemTag);
                            inv.setInventorySlotContents(slotIndex, stackInSlot);

                            if (!stackInSlot.isEmpty()) {
                                int count = stackInSlot.getCount();
                                int maxStack = stackInSlot.getMaxStackSize();

                                sum += count;
                                if (maxStack > 0)
                                    filledSlotsRatio += (double) count / (double) maxStack;
                            }
                        }

                        // 27 slots
                        double percent = (filledSlotsRatio / 27.0f) * 100.0f;
                        // Limit between 0 and 100
                        percent = Math.min(100.0, Math.max(0.0, percent));

                        int redstoneSignal = Container.calcRedstoneFromInventory(inv);

                        TextFormatting color = TextFormatting.GREEN;
                        if (percent > 75.0f)
                            color = TextFormatting.RED;
                        else if (percent > 50.0f)
                            color = TextFormatting.YELLOW;

                        // Capacity
                        event.getToolTip()
                                .add(TextFormatting.GRAY + "Capacity: " + color +
                                        String.format("%.1f", percent)
                                        + "%" + TextFormatting.DARK_GRAY + " (" + sum + "/1728)");
                        // Redstone signal
                        event.getToolTip()
                                .add(TextFormatting.GRAY + "RedStone Signal: " + TextFormatting.GREEN + redstoneSignal);
                    }
                }
            }
        }

    }

}
