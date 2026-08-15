package com.hlysine.create_connected.content.fluidvessel;

import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllSpriteShifts;
import com.zurrtum.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import com.zurrtum.create.client.infrastructure.model.CTModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.core.Direction.Axis;

public class FluidVesselModel extends CTModel {

    public static final FluidVesselCTBehaviour STANDARD = new FluidVesselCTBehaviour(
            AllSpriteShifts.FLUID_TANK, AllSpriteShifts.FLUID_TANK_TOP, AllSpriteShifts.FLUID_TANK_INNER);
    public static final FluidVesselCTBehaviour CREATIVE = new FluidVesselCTBehaviour(
            AllSpriteShifts.CREATIVE_FLUID_TANK, AllSpriteShifts.CREATIVE_CASING, AllSpriteShifts.CREATIVE_CASING);

    public FluidVesselModel(BlockState state, BlockStateModel.UnbakedRoot unbaked, ConnectedTextureBehaviour behaviour) {
        super(state, unbaked, behaviour);
    }

    public static FluidVesselModel standard(BlockState state, BlockStateModel.UnbakedRoot unbaked) {
        return new FluidVesselModel(state, unbaked, STANDARD);
    }

    public static FluidVesselModel creative(BlockState state, BlockStateModel.UnbakedRoot unbaked) {
        return new FluidVesselModel(state, unbaked, CREATIVE);
    }

    @Override
    public void addPartsWithInfo(
            BlockAndTintGetter world,
            BlockPos pos,
            BlockState state,
            RandomSource random,
            List<BlockStateModelPart> parts
    ) {
        int[] indices = createCTData(world, pos, state);
        boolean[] culls = createCullData(world, pos, state.getValue(FluidVesselBlock.AXIS));
        List<BlockStateModelPart> modelParts = new ArrayList<>();
        model.collectParts(random, modelParts);
        for (BlockStateModelPart part : modelParts) {
            QuadCollection.Builder builder = new QuadCollection.Builder();
            for (BakedQuad quad : part.getQuads(null))
                builder.addUnculledFace(replaceQuad(state, random, indices[quad.direction().get3DDataValue()], quad));
            for (Direction direction : Iterate.directions) {
                if (culls[direction.get3DDataValue()])
                    continue;
                addQuads(builder, part, direction, state, random, indices[direction.get3DDataValue()]);
            }
            parts.add(new SimpleModelWrapper(builder.build(), part.useAmbientOcclusion(), part.particleMaterial()));
        }
    }

    protected boolean[] createCullData(BlockAndTintGetter world, BlockPos pos, Axis axis) {
        boolean[] culledFaces = new boolean[6];
        for (Direction face : Iterate.directions) {
            if (face.getAxis() == axis)
                continue;
            culledFaces[face.get3DDataValue()] = ConnectivityHandler.isConnected(world, pos, pos.relative(face));
        }
        return culledFaces;
    }
}
