package github.xevira.mimics;

import github.xevira.mimics.entity.mob.MimicEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mimics implements ModInitializer {
	public static final String MOD_ID = "mimics";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		Registration.load();
		FabricDefaultAttributeRegistry.register(Registration.MIMIC_ENTITY, MimicEntity.createMimicAttributes());

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
			entries.addAfter(Blocks.TRIAL_SPAWNER, Registration.FAKE_SPAWNER);
			entries.addAfter(Blocks.TRIAL_SPAWNER, Registration.MIMIC_CHEST);
		});
	}

	public static Identifier id(String name)
	{
		return Identifier.of(MOD_ID, name);
	}
}