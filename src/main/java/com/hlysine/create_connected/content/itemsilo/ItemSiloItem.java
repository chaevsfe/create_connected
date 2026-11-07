package com.hlysine.create_connected.content.itemsilo;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.item.ItemPlacementSoundContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemSiloItem extends BlockItem {

    public ItemSiloItem(Block p_i48527_1_, Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState placementState) {
        if (!super.placeBlock(context, placementState))
            return false;
        Level level = context.getLevel();
        if (level.getBlockState(context.getClickedPos()).is(placementState.getBlock())) {
            MinecraftServer minecraftserver = level.getServer();
            if (minecraftserver != null) {
                ItemStack itemStack = context.getItemInHand();
                TypedEntityData<BlockEntityType<?>> data = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
                if (data != null) {
                    CompoundTag nbt = data.copyTagWithoutId();
                    nbt.remove("Length");
                    nbt.remove("Size");
                    nbt.remove("Controller");
                    nbt.remove("LastKnownPos");
                    itemStack.set(DataComponents.BLOCK_ENTITY_DATA,
                            TypedEntityData.of(((IBE<?>) getBlock()).getBlockEntityType(), nbt));
                }
            }
        }
        return true;
    }

    private void tryMultiPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isShiftKeyDown())
            return;
        Direction face = ctx.getClickedFace();
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos placedOnPos = pos.relative(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        if (!ItemSiloBlock.isVault(placedOnState))
            return;
        if (SymmetryWandItem.presentInHotbar(player))
            return;
        ItemSiloBlockEntity tankAt = ConnectivityHandler.partAt(CCBlockEntityTypes.ITEM_SILO, world, placedOnPos);
        if (tankAt == null)
            return;
        ItemSiloBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.radius;
        if (width == 1)
            return;

        int tanksToPlace = 0;
        Axis vaultBlockAxis = ItemSiloBlock.getVaultBlockAxis(placedOnState);
        if (vaultBlockAxis == null)
            return;
        if (face.getAxis() != vaultBlockAxis)
            return;

        Direction vaultFacing = Direction.fromAxisAndDirection(vaultBlockAxis, AxisDirection.POSITIVE);
        BlockPos startPos = face == vaultFacing.getOpposite() ? controllerBE.getBlockPos()
                .relative(vaultFacing.getOpposite())
                : controllerBE.getBlockPos()
                .relative(vaultFacing, controllerBE.length);

        if (VecHelper.getCoordinate(startPos, vaultBlockAxis) != VecHelper.getCoordinate(pos, vaultBlockAxis))
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (ItemSiloBlock.isVault(blockState))
                    continue;
                if (!blockState.canBeReplaced())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        ItemPlacementSoundContext context = new ItemPlacementSoundContext(ctx, 0.1f, 1.5f, null);
        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (ItemSiloBlock.isVault(blockState))
                    continue;
                super.place(context.offset(offsetPos, face));
            }
        }
    }

}
