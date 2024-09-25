package github.xevira.mimics.block;

import com.mojang.serialization.MapCodec;
import github.xevira.mimics.Registration;
import github.xevira.mimics.block.entity.MimicChestBlockEntity;
import github.xevira.mimics.entity.mob.MimicEntity;
import github.xevira.mimics.util.ServerTickableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MimicChestBlock extends Block implements BlockEntityProvider {
    private static final VoxelShape DEFAULT_SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.6250, 0.9375),      // Bottom
            VoxelShapes.cuboid(0.0625, 0.6250, 0.0625, 0.9375, 0.8750, 0.9375), // Lid
            VoxelShapes.cuboid(0.4375, 0.4375, 0.0000, 0.5625, 0.6875, 0.0625) // Latch
    ).simplify();
    private static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
    public static final MapCodec<MimicChestBlock> CODEC = createCodec(MimicChestBlock::new);

    static {
        for (Direction direction : Direction.values()) {
            SHAPES.put(direction, calculateShapes(direction, DEFAULT_SHAPE));
        }
    }

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    //public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

    @Override
    protected MapCodec<? extends Block> getCodec() { return CODEC; }

    public MimicChestBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    private static VoxelShape calculateShapes(Direction to, VoxelShape shape) {
        final VoxelShape[] buffer = {shape, VoxelShapes.empty()};

        final int times = (to.getHorizontal() - Direction.NORTH.getHorizontal() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = VoxelShapes.union(buffer[1],
                            VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }


    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    protected static double getOriginY(WorldView world, BlockPos pos, boolean invertY, Box boundingBox) {
        Box box = new Box(pos);
        if (invertY) {
            box = box.stretch(0.0, -1.0, 0.0);
        }

        Iterable<VoxelShape> iterable = world.getCollisions(null, box);
        return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, iterable, invertY ? -2.0 : -1.0);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld)
        {
            Direction facing = state.get(FACING);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MimicChestBlockEntity mimicChestBlockEntity) {
                float health = mimicChestBlockEntity.getHealth();
                float max_health = mimicChestBlockEntity.getMaxHealth();
                // Break block - don't drop anything
                world.breakBlock(pos, false);

                // Spawn the mimic
                //player.sendMessage(Text.literal("You've found a " + Registration.MIMIC_ENTITY.toString() +"!"));  // Temporary

                try {
                    MimicEntity mimic = new MimicEntity(Registration.MIMIC_ENTITY, serverWorld);

                    mimic.setPosition((double) pos.getX() + 0.5, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5);
                    double d = getOriginY(world, pos, false, mimic.getBoundingBox());

                    float yaw = switch (facing) {
                        case EAST -> 270.0f;
                        case SOUTH -> 0.0f;
                        case WEST -> 90.0f;
                        default -> 180.0f;
                    };

                    mimic.refreshPositionAndAngles(
                            (double) pos.getX() + 0.5,
                            (double) pos.getY() + d,
                            (double) pos.getZ() + 0.5,
                            MathHelper.wrapDegrees(yaw),
                            0.0F);

                    mimic.headYaw = mimic.getYaw();
                    mimic.bodyYaw = mimic.getYaw();
                    mimic.initialize(serverWorld, world.getLocalDifficulty(mimic.getBlockPos()), SpawnReason.TRIGGERED, null);
                    mimic.playAmbientSound();

                    if (health > 0.0f)
                        mimic.setHealth(health);    // Set the saved health

                    // Must be in survival or adventure mode
                    if (!player.isCreative() && !player.isSpectator())
                        mimic.setTarget(player);    // Automatically target player that woke it up

                    serverWorld.spawnEntity(mimic);
                } catch (Exception ex) {
                    player.sendMessage(Text.literal("The chest breaks into splinters!"));
                }
            }

            return ActionResult.CONSUME;
        }
        else
            return ActionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return Registration.MIMIC_CHEST_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) { return false; }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ServerTickableBlockEntity.getTicker(world);
    }
}
