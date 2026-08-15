package com.hlysine.create_connected.content.overstressclutch;

import com.hlysine.create_connected.ConnectedLang;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsBoard;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsFormatter;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class OverstressClutchScrollValueBehaviour extends ScrollValueBehaviour<SmartBlockEntity, ServerScrollValueBehaviour> {

    public OverstressClutchScrollValueBehaviour(SmartBlockEntity be) {
        super(ConnectedLang.translateDirect("overstress_clutch.uncouple_delay"), be, new UncoupleDelaySlot());
        withFormatter(OverstressClutchScrollValueBehaviour::format);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(
                label,
                60,
                10,
                CreateLang.translatedOptions("generic.unit", "ticks", "seconds", "minutes"),
                new ValueSettingsFormatter(this::formatSettings)
        );
    }

    public MutableComponent formatSettings(ValueSettings settings) {
        int value = Math.max(1, settings.value());
        return Component.literal(switch (settings.row()) {
            case 0 -> value + "t";
            case 1 -> "0:" + (value < 10 ? "0" : "") + value;
            default -> value + ":00";
        });
    }

    private static String format(int value) {
        if (value < 60)
            return value + "t";
        if (value < 20 * 60)
            return (value / 20) + "s";
        return (value / 20 / 60) + "m";
    }

    public static class UncoupleDelaySlot extends CenteredSideValueBoxTransform {
        public UncoupleDelaySlot() {
            super((state, d) -> {
                Direction.Axis axis = d.getAxis();
                Direction.Axis bearingAxis = state.getValue(RotatedPillarKineticBlock.AXIS);
                return bearingAxis != axis;
            });
        }
    }
}
