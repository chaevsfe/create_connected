package com.hlysine.create_connected.content.copycat;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.foundation.model.BakedModelHelper;
import com.zurrtum.create.client.infrastructure.model.CopycatModel;
import com.zurrtum.create.content.decoration.copycat.CopycatBlock;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleCopycatModel extends CopycatModel {

    public static final AABB UNIT_CUBE = new AABB(BlockPos.ZERO);

    public SimpleCopycatModel(BlockState state, BlockStateModel.UnbakedRoot unbaked) {
        super(state, unbaked);
    }

    protected abstract void collectPieces(BlockState state, List<Piece> pieces);

    @Override
    protected void addPartsWithInfo(
            BlockAndTintGetter world,
            BlockPos pos,
            BlockState state,
            CopycatBlock block,
            BlockState material,
            RandomSource random,
            List<BlockStateModelPart> parts
    ) {
        List<Piece> pieces = new ArrayList<>();
        collectPieces(state, pieces);
        if (pieces.isEmpty())
            return;

        List<BlockStateModelPart> source = getMaterialParts(world, pos, material, random, getModelOf(material));
        if (source.isEmpty())
            return;

        boolean[] uncull = new boolean[6];
        for (Direction direction : Iterate.directions)
            uncull[direction.get3DDataValue()] = block.shouldFaceAlwaysRender(state, direction);

        for (BlockStateModelPart part : source) {
            QuadCollection.Builder builder = new QuadCollection.Builder();

            for (BakedQuad quad : part.getQuads(null))
                for (Piece piece : pieces)
                    if (!piece.cull().isCulled(quad.direction()))
                        builder.addUnculledFace(BakedModelHelper.cropAndMove(quad, piece.crop(), piece.move()));

            for (Direction direction : Iterate.directions) {
                List<BakedQuad> quads = part.getQuads(direction);
                if (quads.isEmpty())
                    continue;
                boolean noCull = uncull[direction.get3DDataValue()];
                for (BakedQuad quad : quads)
                    for (Piece piece : pieces) {
                        if (piece.cull().isCulled(direction))
                            continue;
                        BakedQuad cropped = BakedModelHelper.cropAndMove(quad, piece.crop(), piece.move());
                        if (noCull)
                            builder.addUnculledFace(cropped);
                        else
                            builder.addCulledFace(direction, cropped);
                    }
            }

            parts.add(new SimpleModelWrapper(builder.build(), part.useAmbientOcclusion(), part.particleMaterial()));
        }
    }

    public static Piece piece(int angle, boolean flipY, MutableVec3 offset, MutableAABB select, MutableCullFace cull) {
        select.rotate(angle).flipY(flipY);
        offset.rotate(angle).flipY(flipY);
        cull.rotate(angle).flipY(flipY);
        return new Piece(
                select.toAABB(),
                offset.toVec3().subtract(select.minX / 16f, select.minY / 16f, select.minZ / 16f),
                cull
        );
    }

    public static Piece piece(AABB crop, Vec3 move, MutableCullFace cull) {
        return new Piece(crop, move, cull);
    }

    public static MutableCullFace cull(int mask) {
        return new MutableCullFace(mask);
    }

    public static MutableVec3 vec3(float x, float y, float z) {
        return new MutableVec3(x, y, z);
    }

    public static MutableAABB aabb(float sizeX, float sizeY, float sizeZ) {
        return new MutableAABB(sizeX, sizeY, sizeZ);
    }

    public record Piece(AABB crop, Vec3 move, MutableCullFace cull) {
    }

    public static class MutableCullFace {

        public static final int UP = 2 << Direction.UP.get3DDataValue();
        public static final int DOWN = 2 << Direction.DOWN.get3DDataValue();
        public static final int NORTH = 2 << Direction.NORTH.get3DDataValue();
        public static final int EAST = 2 << Direction.EAST.get3DDataValue();
        public static final int SOUTH = 2 << Direction.SOUTH.get3DDataValue();
        public static final int WEST = 2 << Direction.WEST.get3DDataValue();

        public boolean up;
        public boolean down;
        public boolean north;
        public boolean south;
        public boolean east;
        public boolean west;

        private MutableCullFace(int mask) {
            set((mask & UP) > 0, (mask & DOWN) > 0, (mask & NORTH) > 0, (mask & SOUTH) > 0, (mask & EAST) > 0, (mask & WEST) > 0);
        }

        public MutableCullFace add(Direction direction) {
            return switch (direction) {
                case DOWN -> set(up, true, north, south, east, west);
                case UP -> set(true, down, north, south, east, west);
                case NORTH -> set(up, down, true, south, east, west);
                case SOUTH -> set(up, down, north, true, east, west);
                case WEST -> set(up, down, north, south, east, true);
                case EAST -> set(up, down, north, south, true, west);
            };
        }

        public MutableCullFace rotate(int angle) {
            angle = angle % 360;
            if (angle < 0) angle += 360;
            return switch (angle) {
                case 90 -> set(up, down, west, east, north, south);
                case 180 -> set(up, down, south, north, west, east);
                case 270 -> set(up, down, east, west, south, north);
                default -> this;
            };
        }

        public MutableCullFace flipY(boolean flip) {
            if (!flip) return this;
            return set(down, up, north, south, east, west);
        }

        public boolean isCulled(Direction direction) {
            return switch (direction) {
                case DOWN -> down;
                case UP -> up;
                case NORTH -> north;
                case SOUTH -> south;
                case WEST -> west;
                case EAST -> east;
            };
        }

        public MutableCullFace set(boolean up, boolean down, boolean north, boolean south, boolean east, boolean west) {
            this.up = up;
            this.down = down;
            this.north = north;
            this.south = south;
            this.east = east;
            this.west = west;
            return this;
        }
    }

    public static class MutableVec3 {
        public float x;
        public float y;
        public float z;

        private MutableVec3(float x, float y, float z) {
            set(x, y, z);
        }

        public MutableVec3 rotate(int angle) {
            angle = angle % 360;
            if (angle < 0) angle += 360;
            return switch (angle) {
                case 90 -> set(16 - z, y, x);
                case 180 -> set(16 - x, y, 16 - z);
                case 270 -> set(z, y, 16 - x);
                default -> this;
            };
        }

        public MutableVec3 flipY(boolean flip) {
            if (!flip) return this;
            return set(x, 16 - y, z);
        }

        public Vec3 toVec3() {
            return new Vec3(x / 16f, y / 16f, z / 16f);
        }

        public MutableVec3 set(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }
    }

    public static class MutableAABB {
        public float minX;
        public float minY;
        public float minZ;
        public float maxX;
        public float maxY;
        public float maxZ;

        private MutableAABB(float sizeX, float sizeY, float sizeZ) {
            set(0, 0, 0, sizeX, sizeY, sizeZ);
        }

        public MutableAABB move(float dX, float dY, float dZ) {
            minX += dX;
            maxX += dX;
            minY += dY;
            maxY += dY;
            minZ += dZ;
            maxZ += dZ;
            return this;
        }

        public MutableAABB rotate(int angle) {
            angle = angle % 360;
            if (angle < 0) angle += 360;
            return switch (angle) {
                case 90 -> set(16 - minZ, minY, minX, 16 - maxZ, maxY, maxX);
                case 180 -> set(16 - minX, minY, 16 - minZ, 16 - maxX, maxY, 16 - maxZ);
                case 270 -> set(minZ, minY, 16 - minX, maxZ, maxY, 16 - maxX);
                default -> this;
            };
        }

        public MutableAABB flipY(boolean flip) {
            if (!flip) return this;
            return set(minX, 16 - minY, minZ, maxX, 16 - maxY, maxZ);
        }

        public AABB toAABB() {
            return new AABB(minX / 16f, minY / 16f, minZ / 16f, maxX / 16f, maxY / 16f, maxZ / 16f);
        }

        public MutableAABB set(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            return this;
        }
    }
}
