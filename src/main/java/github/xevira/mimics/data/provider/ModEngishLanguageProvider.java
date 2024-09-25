package github.xevira.mimics.data.provider;

import github.xevira.mimics.Registration;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModEngishLanguageProvider extends FabricLanguageProvider {
    public ModEngishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(Registration.MIMIC_CHEST, "Mimic Chest");
        translationBuilder.add(Registration.FAKE_SPAWNER, "Fake Spawner");
    }
}
