package com.hlysine.create_connected.content.chaincogwheel;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.foundation.render.AllInstanceTypes;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class ChainCogwheelVisual extends KineticBlockEntityVisual<KineticBlockEntity> {

    protected final RotatingInstance rotatingModel;
    @Nullable
    protected final RotatingInstance rotatingTopShaft;
    @Nullable
    protected final RotatingInstance rotatingBottomShaft;

    public ChainCogwheelVisual(VisualizationContext context, KineticBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        rotatingModel = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.chunkPartial(AllPartialModels.SHAFTLESS_COGWHEEL))
                .createInstance();
        rotatingModel.setup(blockEntity).setPosition(getVisualPosition()).rotateToFace(rotationAxis()).setChanged();

        RotatingInstance top = null;
        RotatingInstance bottom = null;

        Block block = blockState.getBlock();
        if (block instanceof IRotate def) {
            for (Direction d : Iterate.directionsInAxis(rotationAxis())) {
                if (!def.hasShaftTowards(blockEntity.getLevel(), blockEntity.getBlockPos(), blockState, d))
                    continue;
                RotatingInstance instance = instancerProvider()
                        .instancer(AllInstanceTypes.ROTATING, Models.chunkPartial(AllPartialModels.SHAFT_HALF))
                        .createInstance();
                instance.setup(blockEntity).setPosition(getVisualPosition()).rotateToFace(Direction.SOUTH, d).setChanged();
                if (d.getAxisDirection() == Direction.AxisDirection.POSITIVE)
                    top = instance;
                else
                    bottom = instance;
            }
        }

        rotatingTopShaft = top;
        rotatingBottomShaft = bottom;
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        switch (blockState.getValue(BlockStateProperties.AXIS)) {
            case X -> setSectionCollector(sectionCollector, 0, -1, -1, 0, 1, 1);
            case Y -> setSectionCollector(sectionCollector, -1, 0, -1, 1, 0, 1);
            default -> setSectionCollector(sectionCollector, -1, -1, 0, 1, 1, 0);
        }
    }

    @Override
    public void update(float partialTick) {
        rotatingModel.setup(blockEntity).setChanged();
        if (rotatingTopShaft != null)
            rotatingTopShaft.setup(blockEntity).setChanged();
        if (rotatingBottomShaft != null)
            rotatingBottomShaft.setup(blockEntity).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(rotatingModel, rotatingTopShaft, rotatingBottomShaft);
    }

    @Override
    protected void _delete() {
        rotatingModel.delete();
        if (rotatingTopShaft != null)
            rotatingTopShaft.delete();
        if (rotatingBottomShaft != null)
            rotatingBottomShaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(rotatingModel);
        consumer.accept(rotatingTopShaft);
        consumer.accept(rotatingBottomShaft);
    }
}
