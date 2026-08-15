package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class CCCreativeTabs {

    public static final ResourceKey<CreativeModeTab> MAIN =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateConnected.asResource("main"));

    public static final List<ItemLike> ITEMS = new ArrayList<>();

    static {
        ITEMS.addAll(List.of(
                CCBlocks.ENCASED_CHAIN_COGWHEEL,
                CCBlocks.CRANK_WHEEL,
                CCBlocks.LARGE_CRANK_WHEEL,
                CCBlocks.INVERTED_CLUTCH,
                CCBlocks.INVERTED_GEARSHIFT,
                CCBlocks.PARALLEL_GEARBOX,
                CCItems.VERTICAL_PARALLEL_GEARBOX,
                CCBlocks.SIX_WAY_GEARBOX,
                CCItems.VERTICAL_SIX_WAY_GEARBOX,
                CCBlocks.BRASS_GEARBOX,
                CCItems.VERTICAL_BRASS_GEARBOX,
                CCBlocks.CROSS_CONNECTOR,
                CCBlocks.SHEAR_PIN,
                CCBlocks.OVERSTRESS_CLUTCH,
                CCBlocks.CENTRIFUGAL_CLUTCH,
                CCBlocks.FREEWHEEL_CLUTCH,
                CCBlocks.BRAKE,
                CCBlocks.KINETIC_BRIDGE,
                CCBlocks.KINETIC_BATTERY,
                CCBlocks.ITEM_SILO,
                CCBlocks.FLUID_VESSEL,
                CCBlocks.CREATIVE_FLUID_VESSEL,
                CCBlocks.INVENTORY_ACCESS_PORT,
                CCBlocks.INVENTORY_BRIDGE,
                CCBlocks.BRASS_CHUTE,
                CCBlocks.DASHBOARD,
                CCBlocks.SEQUENCED_PULSE_GENERATOR,
                CCItems.LINKED_TRANSMITTER,
                CCItems.REDSTONE_LINK_WILDCARD,
                CCBlocks.EMPTY_FAN_CATALYST,
                CCBlocks.FAN_BLASTING_CATALYST,
                CCBlocks.FAN_SMOKING_CATALYST,
                CCBlocks.FAN_SPLASHING_CATALYST,
                CCBlocks.FAN_HAUNTING_CATALYST,
                CCBlocks.FAN_FREEZING_CATALYST,
                CCBlocks.FAN_SEETHING_CATALYST,
                CCBlocks.FAN_SANDING_CATALYST,
                CCBlocks.FAN_ENRICHED_CATALYST,
                CCBlocks.FAN_ENDING_CATALYST_DRAGONS_BREATH,
                CCBlocks.FAN_ENDING_CATALYST_DRAGON_HEAD,
                CCBlocks.FAN_WITHERING_CATALYST,
                CCBlocks.FAN_CHOCOLATE_COATING_CATALYST,
                CCBlocks.FAN_HONEY_COATING_CATALYST,
                CCBlocks.FAN_EXPLODING_CATALYST,
                CCBlocks.FAN_RESONANCE_CATALYST,
                CCBlocks.FAN_SCULKING_CATALYST,
                CCBlocks.FAN_PURIFYING_CATALYST,
                CCBlocks.FAN_TRANSMUTATION_CATALYST,
                CCBlocks.FAN_GLOOMING_CATALYST,
                CCBlocks.FAN_SOUL_STRIPPING_CATALYST
        ));
        ITEMS.addAll(CCBlocks.FAN_DYEING_CATALYSTS.values());
        ITEMS.addAll(List.of(
                CCBlocks.COPYCAT_BLOCK,
                CCBlocks.COPYCAT_SLAB,
                CCBlocks.COPYCAT_BEAM,
                CCBlocks.COPYCAT_VERTICAL_STEP,
                CCBlocks.COPYCAT_STAIRS,
                CCBlocks.COPYCAT_FENCE,
                CCBlocks.COPYCAT_FENCE_GATE,
                CCBlocks.COPYCAT_WALL,
                CCBlocks.COPYCAT_BOARD,
                CCItems.COPYCAT_BOX,
                CCItems.COPYCAT_CATWALK,
                CCItems.CONTROL_CHIP,
                CCItems.MUSIC_DISC_ELEVATOR,
                CCItems.MUSIC_DISC_INTERLUDE
        ));
    }

    public static Identifier getId(ItemLike entry) {
        return BuiltInRegistries.ITEM.getKey(entry.asItem());
    }

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAIN, FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.create_connected.main"))
                .icon(() -> new ItemStack(CCBlocks.BRASS_GEARBOX))
                .displayItems(CCCreativeTabs::buildContents)
                .build());
    }

    private static void buildContents(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        for (ItemLike entry : ITEMS) {
            if (!FeatureToggle.isEnabled(getId(entry)))
                continue;
            Item item = entry.asItem();
            if (item == CCBlocks.KINETIC_BATTERY.asItem()) {
                ItemStack stack = new ItemStack(item);
                stack.set(CCDataComponents.KINETIC_BATTERY_CHARGE, KineticBatteryBlockEntity.getMaxBatteryLevel());
                output.accept(stack);
            } else {
                output.accept(item);
            }
        }
    }
}
