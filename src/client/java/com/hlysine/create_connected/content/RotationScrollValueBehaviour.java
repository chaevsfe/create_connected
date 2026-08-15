package com.hlysine.create_connected.content;

import com.google.common.collect.ImmutableList;
import com.hlysine.create_connected.ConnectedLang;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsBoard;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsFormatter;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class RotationScrollValueBehaviour extends ScrollValueBehaviour<SmartBlockEntity, ServerScrollValueBehaviour> {

    public RotationScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
        withFormatter(v -> String.valueOf(Math.abs(v)));
    }

    public static RotationScrollValueBehaviour centrifugalClutch(SmartBlockEntity be) {
        return new RotationScrollValueBehaviour(
                ConnectedLang.translateDirect("centrifugal_clutch.speed_threshold"),
                be,
                new ClutchValueBox()
        );
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                ConnectedLang.translateDirect("centrifugal_clutch.max_speed"),
                ConnectedLang.translateDirect("centrifugal_clutch.min_speed")
        );
        return new ValueSettingsBoard(label, 256, 32, rows, new ValueSettingsFormatter(this::formatSettings));
    }

    public MutableComponent formatSettings(ValueSettings settings) {
        return CreateLang.text(settings.row() == 0 ? "≤" : "≥")
                .add(CreateLang.number(Math.max(1, Math.abs(settings.value()))))
                .component();
    }
}
