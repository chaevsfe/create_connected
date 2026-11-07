package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.registries.CCBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;

@Deprecated(forRemoval = true, since = "1.3.0")
public class ChargedKineticBatteryItem extends BlockItem {

    public ChargedKineticBatteryItem(Properties builder) {
        super(CCBlocks.KINETIC_BATTERY, builder);
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item self) {
    }

}
