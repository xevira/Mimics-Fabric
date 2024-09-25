package github.xevira.mimics.renderer;

import com.google.common.collect.ImmutableMap;
import github.xevira.mimics.Mimics;
import github.xevira.mimics.block.MimicChestBlock;
import github.xevira.mimics.block.entity.MimicChestBlockEntity;
import github.xevira.mimics.model.MimicChestBlockModel;
import github.xevira.mimics.util.MimicDamage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.Map;

public class MimicChestBlockRenderer implements BlockEntityRenderer<MimicChestBlockEntity> {
    private static final Map<MimicDamage.DamageLevel, Identifier> DAMAGE_TEXTURES = ImmutableMap.of(
            MimicDamage.DamageLevel.NONE,   Mimics.id("textures/entity/mimic_chest.png"),
            MimicDamage.DamageLevel.LOW,    Mimics.id("textures/entity/features/mimic_chest_damaged_low.png"),
            MimicDamage.DamageLevel.MEDIUM, Mimics.id("textures/entity/features/mimic_chest_damaged_medium.png"),
            MimicDamage.DamageLevel.HIGH,   Mimics.id("textures/entity/features/mimic_chest_damaged_high.png")
    );

    private final BlockEntityRendererFactory.Context context;
    private final MimicChestBlockModel model;

    public MimicChestBlockRenderer(BlockEntityRendererFactory.Context context)
    {
        this.context = context;
        this.model = new MimicChestBlockModel(context.getLayerModelPart(MimicChestBlockModel.LAYER_LOCATION));
    }

    @Override
    public void render(MimicChestBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f);

        // Rotate the chest for the correct facing
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(switch (entity.getCachedState().get(MimicChestBlock.FACING)) {
            case EAST -> 270;
            case SOUTH -> 180;
            case WEST -> 90;
            default -> 0;
        }));

        //this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(MimicChestBlockModel.TEXTURE_LOCATION)), light, overlay);

        MimicDamage.DamageLevel damageLevel = entity.getDamageLevel();
        Identifier identifier = DAMAGE_TEXTURES.get(damageLevel);
        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(identifier)), light, overlay);

        matrices.pop();
    }
}
