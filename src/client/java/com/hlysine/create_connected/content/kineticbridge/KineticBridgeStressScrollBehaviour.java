package com.hlysine.create_connected.content.kineticbridge;

import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryValueBox;
import com.zurrtum.create.client.catnip.lang.LangNumberFormat;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;

public class KineticBridgeStressScrollBehaviour extends ScrollValueBehaviour<SmartBlockEntity, ServerScrollValueBehaviour> {

    public KineticBridgeStressScrollBehaviour(SmartBlockEntity be) {
        super(ConnectedLang.translateDirect("kinetic_bridge.stress_impact"), be, new KineticBatteryValueBox(8));
        withFormatter(v -> String.format("%1sx", LangNumberFormat.format(StressImpactScrollValueBehaviour.convertValue(v))));
    }
}
