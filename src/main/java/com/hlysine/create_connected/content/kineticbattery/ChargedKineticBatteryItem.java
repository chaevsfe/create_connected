package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.registries.CCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

@Deprecated(forRemoval = true, since = "1.3.0")
public class ChargedKineticBatteryItem extends BlockItem {

    public ChargedKineticBatteryItem(Properties builder) {
        super(CCBlocks.KINETIC_BATTERY, builder);
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item self) {
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack stack, BlockState state) {
        boolean ret = super.updateCustomBlockEntityTag(pos, world, player, stack, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof KineticBatteryBlockEntity batteryBE))
            return ret;
        batteryBE.setBatteryLevel(KineticBatteryBlockEntity.getMaxBatteryLevel());
        return true;
    }
}
