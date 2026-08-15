package com.hlysine.create_connected.registries;

import com.zurrtum.create.content.decoration.copycat.CopycatBlock;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CCColorHandlers {

    public static void register() {
        BlockColorRegistry.register(List.of(BlockTintSources.water()), CCBlocks.FAN_SPLASHING_CATALYST);

        List<BlockTintSource> copycatTints = List.of(new CopycatTint(0), new CopycatTint(1), new CopycatTint(2));
        BlockColorRegistry.register(copycatTints,
                CCBlocks.COPYCAT_SLAB,
                CCBlocks.COPYCAT_BLOCK,
                CCBlocks.COPYCAT_BEAM,
                CCBlocks.COPYCAT_VERTICAL_STEP,
                CCBlocks.COPYCAT_STAIRS,
                CCBlocks.COPYCAT_FENCE,
                CCBlocks.COPYCAT_WALL,
                CCBlocks.COPYCAT_FENCE_GATE,
                CCBlocks.COPYCAT_BOARD);
    }

    public record CopycatTint(int tintIndex) implements BlockTintSource {

        @Override
        public int color(BlockState state) {
            return GrassColor.getDefaultColor();
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            List<BlockTintSource> tintSources = sourcesOf(level, pos);
            if (tintSources.size() <= tintIndex)
                return -1;
            return tintSources.get(tintIndex).colorInWorld(state, level, pos);
        }

        @Override
        public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            List<BlockTintSource> tintSources = sourcesOf(level, pos);
            if (tintSources.size() <= tintIndex)
                return -1;
            return tintSources.get(tintIndex).colorAsTerrainParticle(state, level, pos);
        }

        private static List<BlockTintSource> sourcesOf(BlockAndTintGetter level, BlockPos pos) {
            return Minecraft.getInstance().getBlockColors().getTintSources(CopycatBlock.getMaterial(level, pos));
        }
    }
}
