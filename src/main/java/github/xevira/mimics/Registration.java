package github.xevira.mimics;

import github.xevira.mimics.block.FakeSpawnerBlock;
import github.xevira.mimics.block.MimicChestBlock;
import github.xevira.mimics.block.entity.FakeSpawnerBlockEntity;
import github.xevira.mimics.block.entity.MimicChestBlockEntity;
import github.xevira.mimics.entity.mob.MimicEntity;
import github.xevira.mimics.world.gen.feature.FakeDungeonFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class Registration {

    // Blocks
    public static final Block MIMIC_CHEST = registerBlockWithItem("mimic_chest",
            new MimicChestBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OAK_TAN)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.5F)
                    .sounds(BlockSoundGroup.WOOD)
                    .burnable()),
            new Item.Settings());

    public static final Block FAKE_SPAWNER = registerBlockWithItem("fake_spawner",
            new FakeSpawnerBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.STONE_GRAY)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .strength(5.0F)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()),
            new Item.Settings());

    // Block Entities
    public static final BlockEntityType<MimicChestBlockEntity> MIMIC_CHEST_BLOCK_ENTITY = registerBlockEntity("mimic_chest",
            BlockEntityType.Builder.create(MimicChestBlockEntity::new, MIMIC_CHEST).build());

    public static final BlockEntityType<FakeSpawnerBlockEntity> FAKE_SPAWNER_BLOCK_ENTITY = registerBlockEntity("fake_spawner",
            BlockEntityType.Builder.create(FakeSpawnerBlockEntity::new, FAKE_SPAWNER).build());

    // Entities
    public static final EntityType<MimicEntity> MIMIC_ENTITY = registerEntity("mimic",
            EntityType.Builder.create(MimicEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.875F, 0.875F)
                    .maxTrackingRange(8));

    // Features
    public static final Feature<DefaultFeatureConfig> MIMIC_ROOM_FEATURE = registerFeature("mimic_room", new FakeDungeonFeature(DefaultFeatureConfig.CODEC));

    // Configured Features
    public static final RegistryKey<ConfiguredFeature<?, ?>> MIMIC_ROOM_CONFIGURED_FEATURE = registerConfiguredFeature("mimic_room");

    // Placed Features
    public static final RegistryKey<PlacedFeature> MIMIC_ROOM_PLACED_FEATURE = registerPlacedFeature("mimic_room");
    public static final RegistryKey<PlacedFeature> MIMIC_ROOM_DEEP_PLACED_FEATURE = registerPlacedFeature("mimic_room_deep");


    // Register functions
    public static <T extends Block> T registerBlock(String name, T block)
    {
        return Registry.register(Registries.BLOCK, Mimics.id(name), block);
    }

    public static <T extends BlockItem> T registerBlockItem(String name, T blockItem)
    {
        return Registry.register(Registries.ITEM, Mimics.id(name), blockItem);
    }

    public static <T extends Block> T registerBlockWithItem(String name, T block, Item.Settings settings)
    {
        T b = registerBlock(name, block);
        registerBlockItem(name, new BlockItem(b, settings));
        return b;
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> be)
    {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Mimics.id(name), be);
    }

    public static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.Builder<T> entity)
    {
        return Registry.register(Registries.ENTITY_TYPE, Mimics.id(name), entity.build());
    }

    public  static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String name, F feature) {
        return Registry.register(Registries.FEATURE, Mimics.id(name), feature);
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerConfiguredFeature(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Mimics.id(name));
    }

    public static RegistryKey<PlacedFeature> registerPlacedFeature(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Mimics.id(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void registerConfiguredFeature(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key,
                                                                                   F feature,
                                                                                   FC featureConfig) {
        context.register(key, new ConfiguredFeature<>(feature, featureConfig));
    }

    public static void bootstrapConfiguredFeatures(Registerable<ConfiguredFeature<?, ?>> context) {
        ConfiguredFeatures.register(context, MIMIC_ROOM_CONFIGURED_FEATURE, MIMIC_ROOM_FEATURE);
    }

    public static void bootstrapPlacedFeatures(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        RegistryEntry<ConfiguredFeature<?, ?>> mimicRoomEntry = registryEntryLookup.getOrThrow(MIMIC_ROOM_CONFIGURED_FEATURE);
        PlacedFeatures.register(
                context,
                MIMIC_ROOM_PLACED_FEATURE,
                mimicRoomEntry,
                CountPlacementModifier.of(10),
                SquarePlacementModifier.of(),
                HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.getTop()),
                BiomePlacementModifier.of()
        );
        PlacedFeatures.register(
                context,
                MIMIC_ROOM_DEEP_PLACED_FEATURE,
                mimicRoomEntry,
                CountPlacementModifier.of(4),
                SquarePlacementModifier.of(),
                HeightRangePlacementModifier.uniform(YOffset.aboveBottom(6), YOffset.fixed(-1)),
                BiomePlacementModifier.of()
        );
    }

    public static void load() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_STRUCTURES,
                MIMIC_ROOM_PLACED_FEATURE
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_STRUCTURES,
                MIMIC_ROOM_DEEP_PLACED_FEATURE
        );
    }
}
