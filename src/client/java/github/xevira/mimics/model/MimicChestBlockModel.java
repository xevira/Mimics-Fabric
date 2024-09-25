package github.xevira.mimics.model;

import com.google.common.collect.ImmutableMap;
import github.xevira.mimics.Mimics;
import github.xevira.mimics.util.MimicDamage;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class MimicChestBlockModel extends Model {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(Mimics.id("main"), "mimic_chest");
    public static final Identifier TEXTURE_LOCATION = Mimics.id("textures/entity/mimic_chest.png");

    private final ModelPart body;
    private final ModelPart top;
    public MimicChestBlockModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.body = root.getChild("body");
        this.top = body.getChild("top");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(0, 19)
                .cuboid(-7.0F, 0.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData top = body.addChild("top", ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-7.0F, 0.0F, -14.0F, 14.0F, 5.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 0)
                .cuboid(-1.0F, -2.0F, -15.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 9.0F, 7.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        body.render(matrices, vertexConsumer, light, overlay, color);
    }
}
