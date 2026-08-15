package com.hlysine.create_connected.content.copycat;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.content.decoration.copycat.CopycatBlockEntity;
import com.zurrtum.create.content.decoration.copycat.WaterloggedCopycatBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MigratingWaterloggedCopycatBlock extends WaterloggedCopycatBlock {

    public MigratingWaterloggedCopycatBlock(Properties pProperties) {
        super(pProperties);
    }

    protected boolean isSelfState(BlockState state) {
        return state.is(this);
    }

    @Override
    public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.COPYCAT;
    }
}
