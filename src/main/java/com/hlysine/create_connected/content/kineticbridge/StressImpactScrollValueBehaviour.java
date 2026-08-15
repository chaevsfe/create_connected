package com.hlysine.create_connected.content.kineticbridge;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class StressImpactScrollValueBehaviour extends ServerScrollValueBehaviour {

    public StressImpactScrollValueBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public static float convertValue(int value) {
        int absoluteValue = Math.abs(value);
        double result = Math.pow(2, absoluteValue / 10.0);
        if (absoluteValue < 40) {
            return (float) result;
        }
        return (int) result;
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int value = Math.max(0, valueSetting.value());
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(Mth.abs(value));
    }

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(0, Math.abs(value));
    }

    @Override
    public String getClipboardKey() {
        return "Stress Impact";
    }

}
