package com.hlysine.create_connected.content.linkedtransmitter;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.redstone.link.ServerLinkBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class LinkedTransmitterBlockEntity extends SmartBlockEntity {

    private int transmittedSignal;
    public boolean containsBase = true;
    private ServerLinkBehaviour link;

    public LinkedTransmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        createLink();
        behaviours.add(link);
    }

    protected void createLink() {
        link = ServerLinkBehaviour.transmitter(this, this::getSignal);
    }

    @Override
    public void initialize() {
        super.initialize();
        transmit(getBlockState().getSignal(getLevel(), getBlockPos(),
                getBlockState().getValue(HorizontalDirectionalBlock.FACING)));
    }

    public int getSignal() {
        return transmittedSignal;
    }

    public void transmit(int strength) {
        transmittedSignal = strength;
        if (link != null)
            link.notifySignalChange();
    }

    @Override
    protected void write(ValueOutput view, boolean clientPacket) {
        view.putInt("Transmit", transmittedSignal);
        super.write(view, clientPacket);
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        super.read(view, clientPacket);
        if (level == null || level.isClientSide() || !link.newPosition)
            transmittedSignal = view.getIntOr("Transmit", 0);
    }
}
