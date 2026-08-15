package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.registries.CCDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity.barComponent;
import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity.getCrudeBatteryLevel;
import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity.getMaxBatteryLevel;
import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity.translate;

public class KineticBatteryBlockItem extends BlockItem {
    public static final int BAR_COLOR = 0x5555FF;

    public KineticBatteryBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getBatteryLevel(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.round(13.0F * Mth.clamp(getBatteryLevel(stack) / getMaxBatteryLevel(), 0, 1));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    public static double getBatteryLevel(ItemStack stack) {
        return stack.getOrDefault(CCDataComponents.KINETIC_BATTERY_CHARGE, 0.0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltip, tooltipFlag);
        double batteryLevel = getBatteryLevel(stack);
        tooltip.accept(translate("battery.charge")
                .withStyle(ChatFormatting.GRAY)
                .append(" ")
                .append(barComponent(0, getCrudeBatteryLevel(batteryLevel, 20), 20)));
        tooltip.accept(Component.literal(" ")
                .append(Component.literal(formatSuHours(batteryLevel)).withStyle(ChatFormatting.BLUE))
                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(formatSuHours(getMaxBatteryLevel()))
                        .append(Component.literal(" "))
                        .append(translate("generic.unit.su_hours"))
                        .withStyle(ChatFormatting.DARK_GRAY)));
    }

    public static String formatSuHours(double level) {
        return String.valueOf(Math.round(level / 3600 / 20));
    }
}
