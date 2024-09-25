package github.xevira.mimics.data.provider;

import github.xevira.mimics.Registration;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public ModEntityTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
                .add(Registration.MIMIC_ENTITY);

        getOrCreateTagBuilder(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                .add(Registration.MIMIC_ENTITY);

        getOrCreateTagBuilder(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)
                .add(Registration.MIMIC_ENTITY);
    }
}
