package github.xevira.mimics.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.world.World;

// Ticks on both the client and the server, with different tickers
public interface DualTickableBlockEntity {
    void clientTick();
    void serverTick();

    static <T extends BlockEntity> BlockEntityTicker<T> getTicker(World pWorld) {
        return pWorld.isClient ?

                (world, pos, state, blockEntity) -> {
                    if (blockEntity instanceof DualTickableBlockEntity tickable) {
                        tickable.clientTick();
                    }
                } :

                (world, pos, state, blockEntity) -> {
                    if (blockEntity instanceof DualTickableBlockEntity tickable) {
                        tickable.serverTick();
                    }
                };
    }

}
