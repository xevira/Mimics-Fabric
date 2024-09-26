package github.xevira.mimics.data.provider;

import github.xevira.mimics.Mimics;
import github.xevira.mimics.Registration;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModEngishLanguageProvider extends FabricLanguageProvider {
    public ModEngishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    private static void addText(@NotNull TranslationBuilder builder, @NotNull Text text, @NotNull String value) {
        if (text.getContent() instanceof TranslatableTextContent translatableTextContent) {
            builder.add(translatableTextContent.getKey(), value);
        } else {
            Mimics.LOGGER.warn("Failed to add translation for text: {}", text.getString());
        }
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(Registration.MIMIC_CHEST, "Mimic Chest");
        translationBuilder.add(Registration.FAKE_SPAWNER, "Fake Spawner");
        addText(translationBuilder, Text.translatable("sound.mimics.mimic_landing"), "Mimic landing");
        addText(translationBuilder, Text.translatable("sound.mimics.mimic_bite"), "Mimic bites");
    }
}
