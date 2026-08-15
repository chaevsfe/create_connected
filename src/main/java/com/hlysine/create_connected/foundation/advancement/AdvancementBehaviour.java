package com.hlysine.create_connected.foundation.advancement;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.api.entity.FakePlayerHandler;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AdvancementBehaviour extends BlockEntityBehaviour<SmartBlockEntity> {

    public static final BehaviourType<AdvancementBehaviour> TYPE = new BehaviourType<>();

    private UUID playerId;
    private final Set<Awardable> advancements;

    public AdvancementBehaviour(SmartBlockEntity be, Awardable... advancements) {
        super(be);
        this.advancements = new HashSet<>();
        add(advancements);
    }

    public void add(Awardable... advancements) {
        this.advancements.addAll(Arrays.asList(advancements));
    }

    public boolean isOwnerPresent() {
        return playerId != null;
    }

    public void setOwner(UUID id) {
        Level level = getLevel();
        if (level == null || level.getPlayerByUUID(id) == null)
            return;
        playerId = id;
        removeAwarded();
        blockEntity.setChanged();
    }

    @Override
    public void initialize() {
        super.initialize();
        removeAwarded();
    }

    private void removeAwarded() {
        Player player = getOwner();
        if (player == null)
            return;
        advancements.removeIf(c -> c.isAlreadyAwardedTo(player));
        if (advancements.isEmpty()) {
            playerId = null;
            blockEntity.setChanged();
        }
    }

    public void awardOwnerIfNear(Awardable advancement, int maxDistance) {
        Player player = getOwner();
        if (player == null)
            return;
        if (player.distanceToSqr(Vec3.atCenterOf(getPos())) > maxDistance * maxDistance)
            return;
        award(advancement, player);
    }

    public void awardOwner(Awardable advancement) {
        Player player = getOwner();
        if (player == null)
            return;
        award(advancement, player);
    }

    private void award(Awardable advancement, Player player) {
        if (advancements.contains(advancement))
            advancement.awardTo(player);
        removeAwarded();
    }

    @Nullable
    private Player getOwner() {
        if (playerId == null)
            return null;
        Level level = getLevel();
        if (level == null)
            return null;
        return level.getPlayerByUUID(playerId);
    }

    @Override
    public void write(ValueOutput view, boolean clientPacket) {
        super.write(view, clientPacket);
        if (playerId != null)
            view.store("Owner", UUIDUtil.CODEC, playerId);
    }

    @Override
    public void read(ValueInput view, boolean clientPacket) {
        super.read(view, clientPacket);
        playerId = view.read("Owner", UUIDUtil.CODEC).orElse(null);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public static void tryAward(BlockGetter reader, BlockPos pos, Awardable advancement) {
        AdvancementBehaviour behaviour = BlockEntityBehaviour.get(reader, pos, TYPE);
        if (behaviour != null)
            behaviour.awardOwner(advancement);
    }

    public static void tryAward(BlockEntity be, Awardable advancement) {
        AdvancementBehaviour behaviour = BlockEntityBehaviour.get(be, TYPE);
        if (behaviour != null)
            behaviour.awardOwner(advancement);
    }

    public static void trackOwner(Level worldIn, BlockPos pos, LivingEntity placer) {
        AdvancementBehaviour behaviour = BlockEntityBehaviour.get(worldIn, pos, TYPE);
        if (behaviour == null)
            return;
        if (!(placer instanceof ServerPlayer player))
            return;
        if (FakePlayerHandler.has(player))
            return;
        behaviour.setOwner(player.getUUID());
    }

    public static void registerAwardables(SmartBlockEntity be, List<BlockEntityBehaviour<?>> behaviours, CCAdvancement... advancements) {
        for (BlockEntityBehaviour<?> behaviour : behaviours) {
            if (behaviour instanceof AdvancementBehaviour ab) {
                ab.add(advancements);
                return;
            }
        }
        behaviours.add(new AdvancementBehaviour(be, advancements));
    }
}
