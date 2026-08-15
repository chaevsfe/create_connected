package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.ConnectedClientLang;
import com.hlysine.create_connected.ConnectedLang;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.GeneratingKineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlock.isDischarging;
import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity.barComponent;
import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity.getCrudeBatteryLevel;
import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity.getMaxBatteryLevel;

public class KineticBatteryTooltipBehaviour extends GeneratingKineticTooltipBehaviour<KineticBatteryBlockEntity> {

    public KineticBatteryTooltipBehaviour(KineticBatteryBlockEntity be) {
        super(be);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        double batteryLevel = blockEntity.getBatteryLevel();

        ConnectedClientLang.translate(
                "battery.status",
                blockEntity.getBatteryStatusTextComponent().withStyle(ChatFormatting.GREEN)
        ).forGoggles(tooltip);

        ConnectedClientLang.builder()
                .add(ConnectedLang.translateDirect("battery.charge")
                        .withStyle(ChatFormatting.GRAY)
                        .append(" ")
                        .append(barComponent(0, getCrudeBatteryLevel(batteryLevel, 20), 20)))
                .forGoggles(tooltip);

        ConnectedClientLang.number(batteryLevel / 3600 / 20)
                .style(ChatFormatting.BLUE)
                .add(ConnectedClientLang.text(" / ").style(ChatFormatting.GRAY))
                .add(ConnectedClientLang.number(getMaxBatteryLevel() / 3600 / 20)
                        .add(Component.literal(" "))
                        .add(ConnectedClientLang.translate("generic.unit.su_hours"))
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        if (isDischarging(blockEntity.getBlockState()) && batteryLevel > 0) {
            ConnectedClientLang.translate("battery.consumption").style(ChatFormatting.GRAY).forGoggles(tooltip);
            if (blockEntity.getRawConsumedStress() == 0 && blockEntity.getConsumedStress() > 0) {
                CreateLang.number(blockEntity.getConsumedStress())
                        .translate("generic.unit.stress")
                        .style(ChatFormatting.BLUE)
                        .space()
                        .add(ConnectedClientLang.translate("battery.powering_belts").style(ChatFormatting.DARK_GRAY))
                        .forGoggles(tooltip, 1);
            } else {
                CreateLang.number(blockEntity.getConsumedStress())
                        .translate("generic.unit.stress")
                        .style(ChatFormatting.BLUE)
                        .forGoggles(tooltip, 1);
            }
        }

        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        return true;
    }
}
