package github.xevira.mimics.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.world.World;

// Only ticks on the Client
public interface ClientTickableBlockEntity {
    void tick();

    static <T extends BlockEntity> BlockEntityTicker<T> getTicker(World pWorld) {
        return pWorld.isClient ? (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof ClientTickableBlockEntity tickableBlockEntity) {
                tickableBlockEntity.tick();
            }
        } : null;
    }
}
