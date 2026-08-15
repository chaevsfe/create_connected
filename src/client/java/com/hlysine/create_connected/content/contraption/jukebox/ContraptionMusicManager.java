package com.hlysine.create_connected.content.contraption.jukebox;

import com.zurrtum.create.catnip.data.Pair;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ContraptionMusicManager {
    private static final Map<Pair<Integer, BlockPos>, SoundInstance> playingContraptionRecords = new HashMap<>();

    public static void playContraptionMusic(@Nullable JukeboxSong song,
                                            AbstractContraptionEntity entity,
                                            BlockPos localPos,
                                            BlockPos worldPos,
                                            boolean silent) {
        Minecraft mc = Minecraft.getInstance();
        Pair<Integer, BlockPos> contraption = Pair.of(entity.getId(), localPos);
        SoundInstance soundInstance = playingContraptionRecords.get(contraption);
        if (soundInstance != null) {
            mc.getSoundManager().stop(soundInstance);
            playingContraptionRecords.remove(contraption);
        }

        if (song != null) {
            if (!silent) {
                mc.gui.hud.setNowPlaying(song.description());
            }

            SoundInstance newInstance = new ContraptionRecordSoundInstance(
                    song.soundEvent().value(),
                    SoundSource.RECORDS,
                    4.0F,
                    1.0F,
                    SoundInstance.createUnseededRandom(),
                    false,
                    0,
                    SoundInstance.Attenuation.LINEAR,
                    entity,
                    localPos
            );
            playingContraptionRecords.put(contraption, newInstance);
            mc.getSoundManager().play(newInstance);
        }

        notifyNearbyEntities(mc.level, worldPos, song != null);
    }

    private static void notifyNearbyEntities(@Nullable ClientLevel level, BlockPos worldPos, boolean playing) {
        if (level == null)
            return;
        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPos).inflate(3)))
            living.setRecordPlayingNearby(worldPos, playing);
    }
}
