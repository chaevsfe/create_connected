package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.FeatureCategory;
import com.hlysine.create_connected.config.FeatureToggle;
import com.hlysine.create_connected.content.brassgearbox.VerticalBrassGearboxItem;
import com.hlysine.create_connected.content.copycat.board.CopycatBoxItem;
import com.hlysine.create_connected.content.copycat.board.CopycatCatwalkItem;
import com.hlysine.create_connected.content.kineticbattery.ChargedKineticBatteryItem;
import com.hlysine.create_connected.content.linkedtransmitter.LinkedTransmitterItem;
import com.hlysine.create_connected.content.parallelgearbox.VerticalParallelGearboxItem;
import com.hlysine.create_connected.content.redstonelinkwildcard.RedstoneLinkWildcardItem;
import com.hlysine.create_connected.content.sixwaygearbox.VerticalSixWayGearboxItem;
import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class CCItems {

    public static final Item CONTROL_CHIP = register("control_chip", Item::new);

    public static final SequencedAssemblyItem INCOMPLETE_CONTROL_CHIP =
            register("incomplete_control_chip", SequencedAssemblyItem::new);

    public static final RedstoneLinkWildcardItem REDSTONE_LINK_WILDCARD =
            toggleable(register("redstone_link_wildcard", RedstoneLinkWildcardItem::new), FeatureCategory.REDSTONE);

    public static final VerticalParallelGearboxItem VERTICAL_PARALLEL_GEARBOX =
            dependent(register("vertical_parallel_gearbox", VerticalParallelGearboxItem::new), "parallel_gearbox");

    public static final VerticalSixWayGearboxItem VERTICAL_SIX_WAY_GEARBOX =
            dependent(register("vertical_six_way_gearbox", VerticalSixWayGearboxItem::new), "six_way_gearbox");

    public static final VerticalBrassGearboxItem VERTICAL_BRASS_GEARBOX =
            dependent(register("vertical_brass_gearbox", VerticalBrassGearboxItem::new), "brass_gearbox");

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true, since = "1.3.0")
    public static final ChargedKineticBatteryItem CHARGED_KINETIC_BATTERY =
            dependent(register("charged_kinetic_battery", ChargedKineticBatteryItem::new), "kinetic_battery");

    public static final LinkedTransmitterItem LINKED_TRANSMITTER =
            toggleable(register("linked_transmitter", LinkedTransmitterItem::new), FeatureCategory.REDSTONE);

    public static final CopycatBoxItem COPYCAT_BOX =
            dependent(register("copycat_box", CopycatBoxItem::new), "copycat_board");

    public static final CopycatCatwalkItem COPYCAT_CATWALK =
            dependent(register("copycat_catwalk", CopycatCatwalkItem::new), "copycat_board");

    public static final Item MUSIC_DISC_ELEVATOR = register("music_disc_elevator", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(CCJukeboxSongs.ELEVATOR));

    public static final Item MUSIC_DISC_INTERLUDE = register("music_disc_interlude", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(CCJukeboxSongs.INTERLUDE));

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
        return register(name, factory, new Item.Properties());
    }

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
        Identifier id = CreateConnected.asResource(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        T item = factory.apply(properties.setId(key));
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return item;
    }

    private static <T extends Item> T toggleable(T item, FeatureCategory... categories) {
        FeatureToggle.register(BuiltInRegistries.ITEM.getKey(item), categories);
        return item;
    }

    private static <T extends Item> T dependent(T item, String dependency) {
        FeatureToggle.registerDependent(BuiltInRegistries.ITEM.getKey(item), CreateConnected.asResource(dependency));
        return item;
    }

    public static void register() {
    }
}
