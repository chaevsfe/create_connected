package com.hlysine.create_connected.content.contraption.jukebox;

import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;

public class ContraptionRecordSoundInstance extends AbstractTickableSoundInstance {
    public WeakReference<AbstractContraptionEntity> contraptionEntity;
    public BlockPos contraptionPos;

    public ContraptionRecordSoundInstance(SoundEvent soundEvent,
                                          SoundSource source,
                                          float volume,
                                          float pitch,
                                          RandomSource random,
                                          boolean looping,
                                          int delay,
                                          SoundInstance.Attenuation attenuation,
                                          AbstractContraptionEntity contraptionEntity,
                                          BlockPos contraptionPos) {
        super(soundEvent, source, random);
        this.volume = volume;
        this.pitch = pitch;
        this.looping = looping;
        this.delay = delay;
        this.attenuation = attenuation;
        this.contraptionEntity = new WeakReference<>(contraptionEntity);
        this.contraptionPos = contraptionPos;
        tick();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        AbstractContraptionEntity entity = contraptionEntity.get();
        if (entity == null || entity.isRemoved()) {
            stop();
            return;
        }
        Vec3 vec = entity.toGlobalVector(Vec3.atCenterOf(contraptionPos), 1);
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
}
