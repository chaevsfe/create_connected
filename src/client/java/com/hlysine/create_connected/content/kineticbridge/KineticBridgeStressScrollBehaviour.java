package com.hlysine.create_connected.content.kineticbridge;

import com.google.common.collect.ImmutableList;
import com.hlysine.create_connected.ConnectedClientLang;
import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryValueBox;
import com.zurrtum.create.client.catnip.lang.LangNumberFormat;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsBoard;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsFormatter;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class KineticBridgeStressScrollBehaviour extends ScrollValueBehaviour<SmartBlockEntity, ServerScrollValueBehaviour> {

    public KineticBridgeStressScrollBehaviour(SmartBlockEntity be) {
        super(ConnectedLang.translateDirect("kinetic_bridge.stress_impact"), be, new KineticBatteryValueBox(8));
        withFormatter(v -> String.format("%1sx", LangNumberFormat.format(StressImpactScrollValueBehaviour.convertValue(v))));
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(ConnectedLang.translateDirect("kinetic_bridge.stress_impact_short"));
        return new ValueSettingsBoard(label, 160, 10, rows, new ValueSettingsFormatter(this::formatSettings));
    }

    public MutableComponent formatSettings(ValueSettings settings) {
        return ConnectedClientLang.number(Math.max(0, StressImpactScrollValueBehaviour.convertValue(settings.value())))
                .add(Component.literal("x"))
                .component();
    }
}
