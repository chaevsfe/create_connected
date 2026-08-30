package com.hlysine.create_connected.content.linkedtransmitter;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import com.zurrtum.create.catnip.animation.LerpedFloat.Chaser;
import com.zurrtum.create.content.redstone.link.ServerLinkBehaviour;
import com.hlysine.create_connected.registries.CCItems;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class LinkedAnalogLeverBlockEntity extends SmartBlockEntity {

    public boolean containsBase = true;

    int state;
    int lastChange;
    public LerpedFloat clientState;

    private ServerLinkBehaviour link;

    public LinkedAnalogLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        clientState = LerpedFloat.linear();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        super.preRemoveSideEffects(pos, oldState);
        if (level == null || level.isClientSide())
            return;
        if (containsBase)
            Block.popResource(level, pos, new ItemStack(CCItems.LINKED_TRANSMITTER));
        if (state != 0)
            LinkedAnalogLeverBlock.updateNeighbors(oldState, level, pos);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        createLink();
        behaviours.add(link);
    }

    protected void createLink() {
        link = ServerLinkBehaviour.transmitter(this, this::getState);
    }

    @Override
    public void initialize() {
        super.initialize();
        transmit();
    }

    public void transmit() {
        if (link != null)
            link.notifySignalChange();
    }

    @Override
    public void write(ValueOutput view, boolean clientPacket) {
        view.putInt("State", state);
        view.putInt("ChangeTimer", lastChange);
        super.write(view, clientPacket);
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        state = view.getIntOr("State", 0);
        lastChange = view.getIntOr("ChangeTimer", 0);
        clientState.chase(state, 0.2f, Chaser.EXP);
        super.read(view, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        if (lastChange > 0) {
            lastChange--;
            if (lastChange == 0) {
                LinkedAnalogLeverBlock.updateNeighbors(getBlockState(), level, worldPosition);
                if (!level.isClientSide()) {
                    transmit();
                    level.setBlock(worldPosition,
                            getBlockState().setValue(LinkedAnalogLeverBlock.POWERED, state > 0), Block.UPDATE_ALL);
                }
            }
        }
        if (level.isClientSide()) {
            clientState.tickChaser();
        }
    }

    public void changeState(boolean back) {
        int prevState = state;
        state += back ? -1 : 1;
        state = Mth.clamp(state, 0, 15);
        if (prevState != state) {
            lastChange = 15;
        }
        sendData();
    }

    public void setState(int value) {
        state = Mth.clamp(value, 0, 15);
        lastChange = 0;
        clientState.chase(state, 0.2f, Chaser.EXP);
        transmit();
        setChanged();
        sendData();
    }

    public int getState() {
        return state;
    }
}
