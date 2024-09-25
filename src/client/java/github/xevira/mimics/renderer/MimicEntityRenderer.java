package github.xevira.mimics.renderer;

import github.xevira.mimics.Mimics;
import github.xevira.mimics.entity.mob.MimicEntity;
import github.xevira.mimics.model.MimicEntityModel;
import github.xevira.mimics.renderer.feature.MimicDamageFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MimicEntityRenderer extends MobEntityRenderer<MimicEntity, MimicEntityModel<MimicEntity>> {
    //private static final Identifier TEXTURE = Mimics.id("textures/entity/mimic.png");

    public MimicEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MimicEntityModel<>(context.getPart(MimicEntityModel.LAYER_LOCATION)), 0.5f);
        this.addFeature(new MimicDamageFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(MimicEntity entity) { return MimicEntityModel.TEXTURE_LOCATION; }
}
