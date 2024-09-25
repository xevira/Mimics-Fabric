package github.xevira.mimics.renderer;

import github.xevira.mimics.block.entity.FakeSpawnerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import java.util.Optional;

public class FakeSpawnerBlockEntityRenderer implements BlockEntityRenderer<FakeSpawnerBlockEntity>{
    private final EntityRenderDispatcher entityRenderDispatcher;

    public FakeSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx)
    {
        this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
    }

    @Override
    public void render(FakeSpawnerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world != null) {
            Entity mob = tryGetEntity(entity.getEntity(), world);

            if (mob != null)
            {
                render(tickDelta, matrices, vertexConsumers, light, mob, this.entityRenderDispatcher, entity.getLastRotation(), entity.getRotation());
            }
        }
    }

    private static Entity tryGetEntity(EntityType<?> type, World world)
    {
        try {
            return type.create(world);
        } catch(RuntimeException ex) {
            return null;
        }
    }

    public static void render(
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            Entity entity,
            EntityRenderDispatcher entityRenderDispatcher,
            double lastRotation,
            double rotation
    ) {
        matrices.push();
        matrices.translate(0.5F, 0.0F, 0.5F);
        float f = 0.53125F;
        float g = Math.max(entity.getWidth(), entity.getHeight());
        if ((double)g > 1.0) {
            f /= g;
        }

        matrices.translate(0.0F, 0.4F, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)MathHelper.lerp((double)tickDelta, lastRotation, rotation) * 10.0F));
        matrices.translate(0.0F, -0.2F, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0F));
        matrices.scale(f, f, f);
        entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, tickDelta, matrices, vertexConsumers, light);
        matrices.pop();
    }

}
