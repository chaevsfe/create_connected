package com.hlysine.create_connected.content.dashboard;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.api.behaviour.display.DisplayHolder;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DashboardBlockEntity extends SmartBlockEntity implements DisplayHolder {

    SignText text = new SignText().setColor(DyeColor.WHITE);
    private @Nullable CompoundTag displayLink;
    int cycleTimer = 0;
    boolean wasDisplaying;
    private static final int LAZY_TICK_RATE = 4;
    private static final int CYCLE_INTERVAL = 40;

    public static Supplier<@Nullable Player> clientPlayer = () -> null;

    public DashboardBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(LAZY_TICK_RATE);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
    }

    @Override
    public @Nullable CompoundTag getDisplayLinkData() {
        return displayLink;
    }

    @Override
    public void setDisplayLinkData(@Nullable CompoundTag data) {
        displayLink = data;
    }

    public SignText getText() {
        return text;
    }

    public void setText(SignText text) {
        this.text = text;
        notifyUpdate();
    }

    public void setLine(int line, Component text) {
        this.setText(this.getText().setMessage(line, text));
    }

    public void clearText() {
        SignText text = this.getText();
        for (int i = 0; i < SignText.LINES; i++) {
            text = text.setMessage(i, Component.empty());
        }
        this.setText(text);
    }

    public int getMaxTextLineWidth() {
        return 90;
    }

    public int getTextLineHeight() {
        return 10;
    }

    public @Nullable BlockPos getSeatPos() {
        if (!getBlockState().getValue(DashboardBlock.OPEN))
            return null;
        return getBlockPos().relative(getBlockState().getValue(DashboardBlock.FACING));
    }

    public @Nullable Component getStatusLine() {
        MutableComponent status = Component.empty();
        boolean needSpacer = false;
        for (int i = 0; i < SignText.LINES; i++) {
            Component line = this.text.getMessage(i, false);
            if (line.getString().isEmpty()) continue;
            if (needSpacer)
                status.append("   ");
            status.append(line).withColor(this.text.getColor().getTextColor());
            needSpacer = true;
        }
        if (!needSpacer)
            return null;
        return status;
    }

    public @Nullable List<Component> getAllDisplays(BlockPos seatPos) {
        List<Component> list = new ArrayList<>(4);
        for (Direction direction : Iterate.horizontalDirections) {
            BlockPos dashboardPos = seatPos.relative(direction);
            if (dashboardPos.equals(getBlockPos())) {
                if (!list.isEmpty()) return null;
                Component status = getStatusLine();
                if (status == null) return null;
                list.add(status);
                continue;
            }
            BlockState state = getLevel().getBlockState(dashboardPos);
            if (state.getBlock() instanceof DashboardBlock && state.getValue(DashboardBlock.FACING) == direction.getOpposite() && state.getValue(DashboardBlock.OPEN)) {
                BlockEntity blockEntity = getLevel().getBlockEntity(dashboardPos);
                if (blockEntity instanceof DashboardBlockEntity dashboard) {
                    Component status = dashboard.getStatusLine();
                    if (status != null)
                        list.add(status);
                }
            }
        }
        return list;
    }

    private boolean displayStatus() {
        BlockPos seatPos = getSeatPos();
        if (seatPos == null)
            return false;

        Player player = clientPlayer.get();
        if (player == null)
            return false;
        if (!player.isPassenger())
            return false;

        Vec3 center = Vec3.atCenterOf(seatPos);
        if (player.distanceToSqr(center) > 1.2)
            return false;
        List<Component> list = getAllDisplays(seatPos);
        if (list == null || list.isEmpty())
            return false;

        Component status = list.get((cycleTimer / CYCLE_INTERVAL) % list.size());
        player.sendOverlayMessage(status);
        cycleTimer += LAZY_TICK_RATE;
        return true;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        if (getLevel().isClientSide()) {
            boolean success = displayStatus();
            if (!success && wasDisplaying) {
                Player player = clientPlayer.get();
                if (player != null) {
                    if (!getBlockState().getValue(DashboardBlock.OPEN))
                        displayOpenStatus(player, false);
                    else
                        player.sendOverlayMessage(Component.empty());
                }
            }
            wasDisplaying = success;
        }
    }

    public static void displayOpenStatus(Player player, boolean open) {
        player.sendOverlayMessage(Component.translatable(open
                ? "create_connected.dashboard.activate_hud"
                : "create_connected.dashboard.deactivate_hud"));
    }

    @Override
    protected void write(ValueOutput view, boolean clientPacket) {
        super.write(view, clientPacket);
        view.store("text", SignText.DIRECT_CODEC, text);
        writeDisplayLink(view);
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        super.read(view, clientPacket);
        view.read("text", SignText.DIRECT_CODEC).ifPresent(signText -> this.text = signText);
        readDisplayLink(view);
    }
}
