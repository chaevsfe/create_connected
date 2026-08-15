package com.hlysine.create_connected.content.overstressclutch;

import com.hlysine.create_connected.ConnectedClientLang;
import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.ClutchState;
import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.item.TooltipHelper;
import net.minecraft.network.chat.Component;

import java.util.List;

import static com.hlysine.create_connected.content.overstressclutch.OverstressClutchBlock.STATE;
import static net.minecraft.ChatFormatting.GOLD;

public class OverstressClutchTooltipBehaviour extends KineticTooltipBehaviour<OverstressClutchBlockEntity> {

    public OverstressClutchTooltipBehaviour(OverstressClutchBlockEntity be) {
        super(be);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToTooltip(tooltip, isPlayerSneaking);

        if (blockEntity.getBlockState().getValue(STATE) == ClutchState.UNCOUPLED) {
            ConnectedClientLang.translate("gui.overstress_clutch.uncoupled").style(GOLD).forGoggles(tooltip);
            Component hint = ConnectedLang.translateDirect("gui.overstress_clutch.uncoupled_explanation");
            for (Component component : TooltipHelper.cutTextComponent(hint, FontHelper.Palette.GRAY_AND_WHITE))
                ConnectedClientLang.builder().add(component.copy()).forGoggles(tooltip);
            added = true;
        }

        return added;
    }
}
