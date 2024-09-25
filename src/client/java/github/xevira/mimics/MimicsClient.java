package github.xevira.mimics;

import github.xevira.mimics.entity.mob.MimicEntity;
import github.xevira.mimics.model.MimicChestBlockModel;
import github.xevira.mimics.model.MimicEntityModel;
import github.xevira.mimics.renderer.FakeSpawnerBlockEntityRenderer;
import github.xevira.mimics.renderer.MimicChestBlockRenderer;
import github.xevira.mimics.renderer.MimicEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

public class MimicsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Block Render Layers
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				Registration.FAKE_SPAWNER);

		// Model Layers
		EntityModelLayerRegistry.registerModelLayer(MimicChestBlockModel.LAYER_LOCATION, MimicChestBlockModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MimicEntityModel.LAYER_LOCATION, MimicEntityModel::getTexturedModelData);

		// Block Entity Renderers
		BlockEntityRendererFactories.register(Registration.MIMIC_CHEST_BLOCK_ENTITY, MimicChestBlockRenderer::new);
		BlockEntityRendererFactories.register(Registration.FAKE_SPAWNER_BLOCK_ENTITY, FakeSpawnerBlockEntityRenderer::new);

		// Entity Renderers
		EntityRendererRegistry.register(Registration.MIMIC_ENTITY, MimicEntityRenderer::new);
	}
}