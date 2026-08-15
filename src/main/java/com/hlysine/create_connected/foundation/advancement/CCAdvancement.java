package com.hlysine.create_connected.foundation.advancement;

import com.hlysine.create_connected.CreateConnected;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class CCAdvancement implements Awardable {

    private final String id;

    @Nullable
    private final SimpleCCTrigger builtinTrigger;

    public CCAdvancement(String id, boolean externalTrigger) {
        this.id = id;
        this.builtinTrigger = externalTrigger ? null : CCTriggers.addSimple(id + "_builtin");
        CCAdvancements.ENTRIES.add(this);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return true;
        AdvancementHolder advancement = sp.level()
                .getServer()
                .getAdvancements()
                .get(CreateConnected.asResource(id));
        if (advancement == null)
            return true;
        return sp.getAdvancements()
                .getOrStartProgress(advancement)
                .isDone();
    }

    @Override
    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return;
        if (builtinTrigger == null)
            throw new UnsupportedOperationException(
                    "Advancement " + id + " uses external Triggers, it cannot be awarded directly");
        builtinTrigger.trigger(sp);
    }
}
