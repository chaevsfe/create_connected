package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.compat.Mods;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import static com.hlysine.create_connected.registries.CCTags.NameSpace.*;

public class CCTags {
    public static <T> TagKey<T> tagKey(ResourceKey<? extends Registry<T>> registry, Identifier id) {
        return TagKey.create(registry, id);
    }

    public static <T> TagKey<T> commonTag(ResourceKey<? extends Registry<T>> registry, String path) {
        return tagKey(registry, Identifier.fromNamespaceAndPath("c", path));
    }

    public static TagKey<Block> commonBlockTag(String path) {
        return commonTag(Registries.BLOCK, path);
    }

    public static TagKey<Item> commonItemTag(String path) {
        return commonTag(Registries.ITEM, path);
    }

    public static TagKey<Fluid> commonFluidTag(String path) {
        return commonTag(Registries.FLUID, path);
    }

    public enum NameSpace {

        MOD(CreateConnected.MODID),
        COPYCATS(Mods.COPYCATS.id()),
        DRAGONS_PLUS(Mods.DRAGONS_PLUS.id());

        public final String id;

        NameSpace(String id) {
            this.id = id;
        }
    }

    public enum Items {

        COPYCAT_BEAM(COPYCATS),
        COPYCAT_BLOCK(COPYCATS),
        COPYCAT_BOARD(COPYCATS),
        COPYCAT_BOX(COPYCATS),
        COPYCAT_CATWALK(COPYCATS),
        COPYCAT_FENCE(COPYCATS),
        COPYCAT_FENCE_GATE(COPYCATS),
        COPYCAT_SLAB(COPYCATS),
        COPYCAT_STAIRS(COPYCATS),
        COPYCAT_VERTICAL_STEP(COPYCATS),
        COPYCAT_WALL(COPYCATS);

        public final TagKey<Item> tag;

        Items(NameSpace namespace) {
            this(namespace, null);
        }

        Items(NameSpace namespace, String path) {
            tag = tagKey(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(namespace.id, path == null ? ConnectedLang.asId(name()) : path));
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Item item) {
            return item.builtInRegistryHolder().is(tag);
        }

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        }
    }

    public enum Fluids {

        FAN_PROCESSING_CATALYSTS_ENDING(DRAGONS_PLUS, "fan_processing_catalysts/ending");

        public final TagKey<Fluid> tag;

        Fluids(NameSpace namespace) {
            this(namespace, null);
        }

        Fluids(NameSpace namespace, String path) {
            tag = tagKey(Registries.FLUID,
                    Identifier.fromNamespaceAndPath(namespace.id, path == null ? ConnectedLang.asId(name()) : path));
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Fluid fluid) {
            return fluid.is(tag);
        }

        public boolean matches(FluidState state) {
            return state.is(tag);
        }
    }
}
