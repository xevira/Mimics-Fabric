package github.xevira.mimics.block;

import com.mojang.serialization.MapCodec;
import github.xevira.mimics.Registration;
import github.xevira.mimics.util.BlockProperties;
import github.xevira.mimics.util.ClientTickableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FakeSpawnerBlock extends BlockWithEntity {
    public static final MapCodec<FakeSpawnerBlock> CODEC = createCodec(FakeSpawnerBlock::new);

    public FakeSpawnerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return Registration.FAKE_SPAWNER_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) { return false; }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ClientTickableBlockEntity.getTicker(world);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
