package com.hlysine.create_connected.content.crankwheel;

import com.hlysine.create_connected.registries.CCPartialModels;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.content.kinetics.crank.HandCrankRenderer;
import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.visual.DynamicVisual;
import com.zurrtum.create.client.flywheel.api.visual.SectionTrackedVisual.SectionCollector;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.instance.InstanceTypes;
import com.zurrtum.create.client.flywheel.lib.instance.TransformedInstance;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.flywheel.lib.visual.SimpleDynamicVisual;
import com.zurrtum.create.client.foundation.render.AllInstanceTypes;
import com.zurrtum.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class CrankWheelVisual extends KineticBlockEntityVisual<CrankWheelBlockEntity> implements SimpleDynamicVisual {
    private final RotatingInstance rotatingModel;
    private final TransformedInstance crank;

    public CrankWheelVisual(VisualizationContext modelManager, CrankWheelBlockEntity blockEntity, float partialTick) {
        super(modelManager, blockEntity, partialTick);

        final boolean isLarge = ICogWheel.isLargeCog(blockEntity.getBlockState());

        crank = instancerProvider().instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(isLarge
                ? CCPartialModels.LARGE_CRANK_WHEEL_HANDLE
                : CCPartialModels.CRANK_WHEEL_HANDLE)
        ).createInstance();

        rotateCrank(partialTick);

        rotatingModel = instancerProvider().instancer(
            AllInstanceTypes.ROTATING,
            Models.chunkPartial(isLarge ? CCPartialModels.LARGE_CRANK_WHEEL_BASE : CCPartialModels.CRANK_WHEEL_BASE)
        ).createInstance();

        rotatingModel.setup(this.blockEntity).setPosition(getVisualPosition())
            .rotateToFace(blockState.getValue(BlockStateProperties.FACING)).setChanged();
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        switch (blockState.getValue(BlockStateProperties.FACING).getAxis()) {
            case X -> setSectionCollector(sectionCollector, 0, -1, -1, 0, 1, 1);
            case Y -> setSectionCollector(sectionCollector, -1, 0, -1, 1, 0, 1);
            default -> setSectionCollector(sectionCollector, -1, -1, 0, 1, 1, 0);
        }
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        rotateCrank(ctx.partialTick());
    }

    private void rotateCrank(float pt) {
        var facing = blockState.getValue(BlockStateProperties.FACING);
        float angle = AngleHelper.rad(HandCrankRenderer.getHandCrankIndependentAngle(blockEntity, pt));

        crank.setIdentityTransform().translate(getVisualPosition()).center()
            .rotate(angle, Direction.get(Direction.AxisDirection.POSITIVE, facing.getAxis()))
            .rotate(new Quaternionf().rotateTo(0, 0, -1, facing.getStepX(), facing.getStepY(), facing.getStepZ()))
            .uncenter().setChanged();
    }

    @Override
    protected void _delete() {
        crank.delete();
        rotatingModel.delete();
    }

    @Override
    public void update(float pt) {
        rotatingModel.setup(blockEntity).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(crank, rotatingModel);
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(crank);
        consumer.accept(rotatingModel);
    }
}
