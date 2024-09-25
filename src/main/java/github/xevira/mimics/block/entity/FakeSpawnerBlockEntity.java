package github.xevira.mimics.block.entity;

import github.xevira.mimics.Registration;
import github.xevira.mimics.util.ClientTickableBlockEntity;
import github.xevira.mimics.util.ServerTickableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FakeSpawnerBlockEntity extends BlockEntity implements ClientTickableBlockEntity {

    public static final int PLAYER_RANGE = 16;

    // Need a mob entity to render?
    private EntityType<?> entity;

    private double lastRotation;
    private double rotation;

    public FakeSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.FAKE_SPAWNER_BLOCK_ENTITY, pos, state);
        this.entity = EntityType.ZOMBIE;    // Default to zombie
    }

    public void setEntity(EntityType<?> entity)
    {
        this.entity = entity;
    }

    public EntityType<?> getEntity() { return this.entity; }

    public double getLastRotation() { return this.lastRotation; }
    public double getRotation() { return this.rotation; }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("spawnerEntity"))
        {
            String id = nbt.getString("spawnerEntity");

            this.entity = Registries.ENTITY_TYPE.get(Identifier.of(id));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (this.entity != null)
            nbt.putString("spawnerEntity", EntityType.getId(this.entity).toString());
    }

    @Override
    // Client-side
    public void tick() {
        if (this.world == null || !this.isPlayerInRange(this.world, this.pos)) {
            this.lastRotation = this.rotation;
        } else if (this.entity != null) {
            Random random = world.getRandom();
            double d = (double)this.pos.getX() + random.nextDouble();
            double e = (double)this.pos.getY() + random.nextDouble();
            double f = (double)this.pos.getZ() + random.nextDouble();
            world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);

            this.lastRotation = this.rotation;
            this.rotation = (this.rotation + 1.0) % 360.0;
        }
    }

    private boolean isPlayerInRange(World world, BlockPos pos) {
        return world.isPlayerInRange((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)PLAYER_RANGE);
    }
}
