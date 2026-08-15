package com.hlysine.create_connected.content.kineticbattery;

import com.zurrtum.create.content.redstone.displayLink.DisplayLinkContext;
import com.zurrtum.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.zurrtum.create.content.trains.display.FlapDisplayBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class KineticBatteryDisplaySource extends PercentOrProgressBarDisplaySource {

    @Override
    protected String getTranslationKey() {
        return "kinetic_battery";
    }

    @Override
    public boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected Float getProgress(DisplayLinkContext context) {
        BlockEntity entity = context.getSourceBlockEntity();
        if (!(entity instanceof KineticBatteryBlockEntity kbe))
            return null;
        return (float) (kbe.getBatteryLevel() / KineticBatteryBlockEntity.getMaxBatteryLevel());
    }

    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        if (context.sourceConfig().getIntOr("Mode", 0) == 1)
            return super.formatNumeric(context, currentLevel);
        MutableComponent value = Component.literal(
                String.valueOf(Math.round(currentLevel * KineticBatteryBlockEntity.getMaxBatteryLevel() / 3600 / 20)));
        if (context.getTargetBlockEntity() instanceof FlapDisplayBlockEntity)
            value.append(Component.literal(" "));
        return value.append(KineticBatteryBlockEntity.translate("generic.unit.su_hours"));
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return context.sourceConfig().getIntOr("Mode", 0) == 2;
    }
}
