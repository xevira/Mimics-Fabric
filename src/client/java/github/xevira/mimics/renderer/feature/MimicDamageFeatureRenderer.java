package github.xevira.mimics.renderer.feature;

import com.google.common.collect.ImmutableMap;
import github.xevira.mimics.Mimics;
import github.xevira.mimics.entity.mob.MimicEntity;
import github.xevira.mimics.model.MimicEntityModel;
import github.xevira.mimics.util.MimicDamage;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.util.Identifier;

import java.util.Map;

public class MimicDamageFeatureRenderer extends FeatureRenderer<MimicEntity, MimicEntityModel<MimicEntity>> {
    private static final Map<MimicDamage.DamageLevel, Identifier> DAMAGE_TEXTURES = ImmutableMap.of(
            MimicDamage.DamageLevel.LOW,
            Mimics.id("textures/entity/features/mimic_damaged_low.png"),
            MimicDamage.DamageLevel.MEDIUM,
            Mimics.id("textures/entity/features/mimic_damaged_medium.png"),
            MimicDamage.DamageLevel.HIGH,
            Mimics.id("textures/entity/features/mimic_damaged_high.png")
    );

    public MimicDamageFeatureRenderer(FeatureRendererContext<MimicEntity, MimicEntityModel<MimicEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int light,
                       MimicEntity entity,
                       float limbAngle,
                       float limbDistance,
                       float tickDelta,
                       float animationProgress,
                       float headYaw,
                       float headPitch) {
        if (!entity.isInvisible()) {
            MimicDamage.DamageLevel damageLevel = entity.getDamageLevel();
            if (damageLevel != MimicDamage.DamageLevel.NONE) {
                Identifier identifier = (Identifier)DAMAGE_TEXTURES.get(damageLevel);
                renderModel(this.getContextModel(), identifier, matrices, vertexConsumers, light, entity, -1);
            }
        }

    }
}
