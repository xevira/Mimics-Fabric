package github.xevira.mimics.model;

import github.xevira.mimics.Mimics;
import github.xevira.mimics.animation.ModAnimations;
import github.xevira.mimics.entity.mob.MimicEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class MimicEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(Mimics.id("main"), "mimic");
    public static final Identifier TEXTURE_LOCATION = Mimics.id("textures/entity/mimic.png");

    private final ModelPart body;
    private final ModelPart top;

    public MimicEntityModel(ModelPart root)
    {
        super(RenderLayer::getEntityCutout);
        this.body = root.getChild("body");
        this.top = this.body.getChild("top");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 19).cuboid(-7.0F, -10.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData top = body.addChild("top", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0F, -4.0F, -14.0F, 14.0F, 5.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.0F, -1.0F, -15.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(3.0F, 0.0F, -12.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-5.0F, 0.0F, -12.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -10.0F, 7.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }


    @Override
    public ModelPart getPart() {
        return this.body;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        MimicEntity mimic = (MimicEntity)entity;

        this.updateAnimation(mimic.jumpAnimationState, ModAnimations.MIMIC_JUMP, animationProgress, 1f);
        this.updateAnimation(mimic.attackingAnimationState, ModAnimations.MIMIC_ATTACKING, animationProgress, 1f);
    }
}
