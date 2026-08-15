package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.content.copycat.beam.CopycatBeamModel;
import com.hlysine.create_connected.content.copycat.block.CopycatBlockModel;
import com.hlysine.create_connected.content.copycat.board.CopycatBoardModel;
import com.hlysine.create_connected.content.copycat.fence.CopycatFenceModel;
import com.hlysine.create_connected.content.copycat.fencegate.CopycatFenceGateModel;
import com.hlysine.create_connected.content.copycat.slab.CopycatSlabModel;
import com.hlysine.create_connected.content.copycat.stairs.CopycatStairsModel;
import com.hlysine.create_connected.content.copycat.verticalstep.CopycatVerticalStepModel;
import com.hlysine.create_connected.content.copycat.wall.CopycatWallModel;
import com.hlysine.create_connected.content.crossconnector.EncasedCrossConnectorBlock;
import com.hlysine.create_connected.content.brassgearbox.BrassGearboxBlock;
import com.hlysine.create_connected.content.fluidvessel.FluidVesselModel;
import com.hlysine.create_connected.content.itemsilo.ItemSiloCTBehaviour;
import com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxBlock;
import com.zurrtum.create.client.AllCTBehaviours;
import com.zurrtum.create.client.AllCasings;
import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.client.AllSpriteShifts;
import com.zurrtum.create.client.infrastructure.model.BracketedKineticBlockModel;
import com.zurrtum.create.client.infrastructure.model.CTModel;

public class CCModels {

    public static void register() {
        AllModels.register(CCBlocks.COPYCAT_SLAB, CopycatSlabModel::new);
        AllModels.register(CCBlocks.COPYCAT_BLOCK, CopycatBlockModel::new);
        AllModels.register(CCBlocks.COPYCAT_BEAM, CopycatBeamModel::new);
        AllModels.register(CCBlocks.COPYCAT_VERTICAL_STEP, CopycatVerticalStepModel::new);
        AllModels.register(CCBlocks.COPYCAT_STAIRS, CopycatStairsModel::new);
        AllModels.register(CCBlocks.COPYCAT_FENCE, CopycatFenceModel::new);
        AllModels.register(CCBlocks.COPYCAT_WALL, CopycatWallModel::new);
        AllModels.register(CCBlocks.COPYCAT_FENCE_GATE, CopycatFenceGateModel::new);
        AllModels.register(CCBlocks.COPYCAT_BOARD, CopycatBoardModel::new);

        AllModels.register(CCBlocks.FLUID_VESSEL, FluidVesselModel::standard);
        AllModels.register(CCBlocks.CREATIVE_FLUID_VESSEL, FluidVesselModel::creative);

        AllModels.register(CCBlocks.ITEM_SILO, CTModel.of(new ItemSiloCTBehaviour()));
        AllModels.register(CCBlocks.SHEAR_PIN, BracketedKineticBlockModel::new);

        AllModels.register(CCBlocks.PARALLEL_GEARBOX, CTModel.of(AllCTBehaviours.ANDESITE_CASING));
        AllCasings.make(CCBlocks.PARALLEL_GEARBOX, AllSpriteShifts.ANDESITE_CASING,
                (s, f) -> f.getAxis() == s.getValue(ParallelGearboxBlock.AXIS));

        AllModels.register(CCBlocks.BRASS_GEARBOX, CTModel.of(AllCTBehaviours.BRASS_CASING));
        AllCasings.make(CCBlocks.BRASS_GEARBOX, AllSpriteShifts.BRASS_CASING,
                (s, f) -> f.getAxis() == s.getValue(BrassGearboxBlock.AXIS));

        AllModels.register(CCBlocks.ANDESITE_ENCASED_CROSS_CONNECTOR, CTModel.of(AllCTBehaviours.ANDESITE_CASING));
        AllCasings.make(CCBlocks.ANDESITE_ENCASED_CROSS_CONNECTOR, AllSpriteShifts.ANDESITE_CASING,
                (s, f) -> f.getAxis() == s.getValue(EncasedCrossConnectorBlock.AXIS));

        AllModels.register(CCBlocks.BRASS_ENCASED_CROSS_CONNECTOR, CTModel.of(AllCTBehaviours.BRASS_CASING));
        AllCasings.make(CCBlocks.BRASS_ENCASED_CROSS_CONNECTOR, AllSpriteShifts.BRASS_CASING,
                (s, f) -> f.getAxis() == s.getValue(EncasedCrossConnectorBlock.AXIS));
    }
}
