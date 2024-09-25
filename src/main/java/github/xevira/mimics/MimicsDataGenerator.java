package github.xevira.mimics;

import github.xevira.mimics.data.generator.ModWorldGenerator;
import github.xevira.mimics.data.provider.ModBlockLootTableProvider;
import github.xevira.mimics.data.provider.ModBlockTagProvider;
import github.xevira.mimics.data.provider.ModEngishLanguageProvider;
import github.xevira.mimics.data.provider.ModEntityTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class MimicsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModEntityTagProvider::new);
		pack.addProvider(ModWorldGenerator::new);
		pack.addProvider(ModEngishLanguageProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, Registration::bootstrapConfiguredFeatures);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, Registration::bootstrapPlacedFeatures);
	}
}
