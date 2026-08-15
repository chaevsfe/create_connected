package com.hlysine.create_connected.content.shearpin;

import com.hlysine.create_connected.registries.CCPartialModels;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.model.Models;

public class ShearPinVisual extends SingleAxisRotatingVisual<ShearPinBlockEntity> {
    public ShearPinVisual(VisualizationContext context, ShearPinBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.chunkPartial(CCPartialModels.SHEAR_PIN));
    }
}
