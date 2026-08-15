package com.hlysine.create_connected.content.fluidvessel;

import com.hlysine.create_connected.config.CServer;
import com.zurrtum.create.AllAdvancements;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllSoundEvents;
import com.zurrtum.create.api.boiler.BoilerHeater;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.decoration.steamWhistle.WhistleBlock;
import com.zurrtum.create.content.decoration.steamWhistle.WhistleBlockEntity;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.content.fluids.tank.SoundPool;
import com.zurrtum.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.zurrtum.create.foundation.advancement.AdvancementBehaviour;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public class BoilerData extends com.zurrtum.create.content.fluids.tank.BoilerData {

    private static final int SAMPLE_RATE = 5;
    private static final float passiveEngineEfficiency = 1 / 8f;

    private int gatheredSupply;
    private final float[] supplyOverTime = new float[10];
    private int ticksUntilNextSample;
    private int currentIndex;
    private int configLevelCap = 18;

    private final SoundPool.Sound sound = (level, pos) -> {
        float volume = 3f / Math.max(2, attachedEngines / 6);
        float pitch = 1.18f - level.getRandom().nextFloat() * .25f;
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, volume, pitch, false);

        AllSoundEvents.STEAM.playAt(level, pos, volume / 16, .8f, false);
    };
    private final EnumMap<Direction, SoundPool> pools = new EnumMap<>(Direction.class);

    @Override
    public void tick(FluidTankBlockEntity controller) {
        if (!isActive())
            return;

        configLevelCap = CServer.VesselMaxLevel.get();

        Level level = controller.getLevel();
        if (level.isClientSide()) {
            pools.values().forEach(p -> p.play(level));
            gauge.tickChaser();
            float current = gauge.getValue(1);
            if (current > 1 && level.getRandom().nextFloat() < 1 / 2f)
                gauge.setValueNoUpdate(current + Math.min(-(current - 1) * level.getRandom().nextFloat(), 0));
            return;
        }
        if (needsHeatLevelUpdate && updateTemperature(controller))
            controller.notifyUpdate();
        ticksUntilNextSample--;
        if (ticksUntilNextSample > 0)
            return;
        int capacity = controller.getTankInventory().getMaxAmountPerStack();
        if (capacity == 0)
            return;

        ticksUntilNextSample = SAMPLE_RATE;
        supplyOverTime[currentIndex] = gatheredSupply / (float) SAMPLE_RATE;
        waterSupply = Math.max(waterSupply, supplyOverTime[currentIndex]);
        currentIndex = (currentIndex + 1) % supplyOverTime.length;
        gatheredSupply = 0;

        if (currentIndex == 0) {
            waterSupply = 0;
            for (float i : supplyOverTime)
                waterSupply = Math.max(i, waterSupply);
        }

        if (controller instanceof CreativeFluidVesselBlockEntity)
            waterSupply = waterSupplyPerLevel * 20;

        if (getActualHeat(controller.getTotalTankSize()) == 18)
            controller.award(AllAdvancements.STEAM_ENGINE_MAXED);

        controller.notifyUpdate();
    }

    @Override
    public void updateOcclusion(FluidTankBlockEntity base) {
        if (!(base instanceof FluidVesselBlockEntity controller)) {
            super.updateOcclusion(base);
            return;
        }
        if (!controller.getLevel().isClientSide())
            return;
        if (attachedEngines + attachedWhistles == 0)
            return;
        for (Direction d : Iterate.horizontalDirections) {
            boolean alongLength = d.getAxis() == controller.getAxis();
            int size = alongLength ? controller.getHeight() : controller.getWidth();
            AABB aabb =
                    new AABB(controller.getBlockPos()).move(size / 2f - .5f, 0, size / 2f - .5f)
                            .deflate(5f / 8);
            aabb = aabb.move(d.getStepX() * (size / 2f + 1 / 4f), 0,
                    d.getStepZ() * (size / 2f + 1 / 4f));
            aabb = aabb.inflate(Math.abs(d.getStepZ()) / 2f, 0.25f, Math.abs(d.getStepX()) / 2f);
            occludedDirections[d.get2DDataValue()] = !controller.getLevel()
                    .noCollision(aabb);
        }
    }

    @Override
    public void queueSoundOnSide(BlockPos pos, Direction side) {
        SoundPool pool = pools.get(side);
        if (pool == null) {
            pool = new SoundPool(4, 2, sound);
            pools.put(side, pool);
        }
        pool.queueAt(pos);
    }

    @Override
    public float getEngineEfficiency(int boilerSize) {
        float multiplier = CServer.VesselBoilerStressMultiplier.get().floatValue();
        if (isPassive(boilerSize))
            return passiveEngineEfficiency / attachedEngines * multiplier;
        if (activeHeat == 0)
            return 0;
        int actualHeat = getActualHeat(boilerSize);
        return (attachedEngines <= actualHeat ? 1 : (float) actualHeat / attachedEngines) * multiplier;
    }

    private int getActualHeat(int boilerSize) {
        int forBoilerSize = getMaxHeatLevelForBoilerSize(boilerSize);
        int forWaterSupply = getMaxHeatLevelForWaterSupply();
        return Math.min(Math.min(activeHeat, Math.min(forWaterSupply, forBoilerSize)), configLevelCap);
    }

    @Override
    public void calcMinMaxForSize(int boilerSize) {
        maxHeatForSize = getMaxHeatLevelForBoilerSize(boilerSize);
        maxHeatForWater = getMaxHeatLevelForWaterSupply();

        minValue = Math.min(configLevelCap, Math.min(passiveHeat ? 1 : activeHeat, Math.min(maxHeatForWater, maxHeatForSize)));
        maxValue = Math.max(passiveHeat ? 1 : activeHeat, Math.max(maxHeatForWater, maxHeatForSize));
    }

    @Override
    public MutableComponent getHeatLevelTextComponent() {
        int boilerLevel = Math.min(Math.min(activeHeat, Math.min(maxHeatForWater, maxHeatForSize)), configLevelCap);

        return isPassive() ? Component.translatable("create.boiler.passive")
                : boilerLevel == 0 ? Component.translatable("create.boiler.idle")
                : boilerLevel == 18 ? Component.translatable("create.boiler.max_lvl")
                : Component.translatable("create.boiler.lvl", String.valueOf(boilerLevel));
    }

    public int getConfigLevelCap() {
        return configLevelCap;
    }

    @Override
    public boolean evaluate(FluidTankBlockEntity base) {
        if (!(base instanceof FluidVesselBlockEntity controller))
            return super.evaluate(base);

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        configLevelCap = CServer.VesselMaxLevel.get();
        int prevEngines = attachedEngines;
        int prevWhistles = attachedWhistles;
        attachedEngines = 0;
        attachedWhistles = 0;

        Axis axis = controller.getAxis();
        for (int yOffset = 0; yOffset < controller.getWidth(); yOffset++) {
            for (int lengthOffset = 0; lengthOffset < controller.getHeight(); lengthOffset++) {
                for (int widthOffset = 0; widthOffset < controller.getWidth(); widthOffset++) {

                    BlockPos pos = controllerPos.offset(
                            axis == Axis.X ? lengthOffset : widthOffset,
                            yOffset,
                            axis == Axis.Z ? lengthOffset : widthOffset
                    );
                    BlockState blockState = level.getBlockState(pos);
                    if (!FluidVesselBlock.isVessel(blockState))
                        continue;
                    for (Direction d : Iterate.directions) {
                        BlockPos attachedPos = pos.relative(d);
                        BlockState attachedState = level.getBlockState(attachedPos);
                        if (attachedState.is(AllBlocks.STEAM_ENGINE) && SteamEngineBlock.getFacing(attachedState) == d)
                            attachedEngines++;
                        if (attachedState.is(AllBlocks.STEAM_WHISTLE)
                                && WhistleBlock.getAttachedDirection(attachedState)
                                .getOpposite() == d)
                            attachedWhistles++;
                    }
                }
            }
        }

        needsHeatLevelUpdate = true;
        return prevEngines != attachedEngines || prevWhistles != attachedWhistles;
    }

    @Override
    public void checkPipeOrganAdvancement(FluidTankBlockEntity base) {
        if (!(base instanceof FluidVesselBlockEntity controller)) {
            super.checkPipeOrganAdvancement(base);
            return;
        }

        AdvancementBehaviour behaviour = controller.getBehaviour(AdvancementBehaviour.TYPE);
        if (behaviour == null || !behaviour.isOwnerPresent())
            return;

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        Set<Integer> whistlePitches = new HashSet<>();

        Axis axis = controller.getAxis();
        for (int yOffset = 0; yOffset < controller.getWidth(); yOffset++) {
            for (int lengthOffset = 0; lengthOffset < controller.getHeight(); lengthOffset++) {
                for (int widthOffset = 0; widthOffset < controller.getWidth(); widthOffset++) {

                    BlockPos pos = controllerPos.offset(
                            axis == Axis.X ? lengthOffset : widthOffset,
                            yOffset,
                            axis == Axis.Z ? lengthOffset : widthOffset
                    );
                    BlockState blockState = level.getBlockState(pos);
                    if (!FluidVesselBlock.isVessel(blockState))
                        continue;
                    for (Direction d : Iterate.directions) {
                        BlockPos attachedPos = pos.relative(d);
                        BlockState attachedState = level.getBlockState(attachedPos);
                        if (attachedState.is(AllBlocks.STEAM_WHISTLE)
                                && WhistleBlock.getAttachedDirection(attachedState)
                                .getOpposite() == d) {
                            if (level.getBlockEntity(attachedPos) instanceof WhistleBlockEntity wbe)
                                whistlePitches.add(wbe.getPitchId());
                        }
                    }
                }
            }
        }

        if (whistlePitches.size() >= 12)
            controller.award(AllAdvancements.PIPE_ORGAN);
    }

    @Override
    public boolean updateTemperature(FluidTankBlockEntity base) {
        if (!(base instanceof FluidVesselBlockEntity controller))
            return super.updateTemperature(base);

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        needsHeatLevelUpdate = false;

        boolean prevPassive = passiveHeat;
        int prevActive = activeHeat;
        passiveHeat = false;
        activeHeat = 0;

        Axis axis = controller.getAxis();
        for (int lengthOffset = 0; lengthOffset < controller.getHeight(); lengthOffset++) {
            for (int widthOffset = 0; widthOffset < controller.getWidth(); widthOffset++) {
                BlockPos pos = controllerPos.offset(
                        axis == Axis.X ? lengthOffset : widthOffset,
                        -1,
                        axis == Axis.Z ? lengthOffset : widthOffset
                );
                BlockState blockState = level.getBlockState(pos);
                float heat = BoilerHeater.findHeat(level, pos, blockState);
                if (heat == 0) {
                    passiveHeat = true;
                } else if (heat > 0) {
                    activeHeat += heat;
                }
            }
        }

        activeHeat = Math.max(0, Math.min(18, (int) Math.floor(activeHeat * CServer.VesselHeatMultiplier.get().floatValue())));
        passiveHeat &= activeHeat == 0;

        return prevActive != activeHeat || prevPassive != passiveHeat;
    }

    @Override
    public void clear() {
        waterSupply = 0;
        activeHeat = 0;
        passiveHeat = false;
        attachedEngines = 0;
        Arrays.fill(supplyOverTime, 0);
    }

    @Override
    public BoilerFluidHandler createHandler() {
        return new VesselBoilerFluidHandler();
    }

    public class VesselBoilerFluidHandler extends BoilerFluidHandler {

        private int filled;

        @Override
        public void setStack(int slot, FluidStack stack) {
            filled += stack.getAmount();
        }

        @Override
        public void markDirty() {
            if (filled > 0) {
                gatheredSupply += filled;
                filled = 0;
            }
        }

    }

}
