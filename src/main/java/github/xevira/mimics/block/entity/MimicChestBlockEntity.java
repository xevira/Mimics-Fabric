package github.xevira.mimics.block.entity;

import github.xevira.mimics.Mimics;
import github.xevira.mimics.Registration;
import github.xevira.mimics.util.MimicDamage;
import github.xevira.mimics.util.ServerTickableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MimicChestBlockEntity extends BlockEntity implements ServerTickableBlockEntity {
    public static final float HEAL_CHANCE = 0.003f;        // 0.3% chance per tick per level of difficulty

    private float current_health = 0.0f;    // If positive, will update the mimic's health upon activation
    private float max_health = 0.0f;

    public MimicChestBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.MIMIC_CHEST_BLOCK_ENTITY, pos, state);
    }

    public void setHealth(float health, float max_health)
    {
        this.current_health = health;
        this.max_health = max_health;
        markDirty();
    }

    public float getHealth()
    {
        return this.current_health;
    }

    public float getMaxHealth()
    {
        return this.max_health;
    }

    public MimicDamage.DamageLevel getDamageLevel() {
        if (this.current_health > 0.0f && this.max_health > 0.0f)
        {
            return MimicDamage.MIMIC.getMimicDamageLevel(this.current_health / this.max_health);
        }
        return MimicDamage.DamageLevel.NONE;
    }


    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("current_health"))
            this.current_health = nbt.getFloat("current_health");

        if (nbt.contains("max_health"))
            this.max_health = nbt.getFloat("max_health");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (this.current_health > 0.0f)
            nbt.putFloat("current_health", this.current_health);
        if (this.max_health > 0.0f)
            nbt.putFloat("max_health", this.max_health);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeNbt(nbt, registryLookup);
        if (this.current_health > 0.0f)
            nbt.putFloat("current_health", this.current_health);
        if (this.max_health > 0.0f)
            nbt.putFloat("max_health", this.max_health);
        return nbt;
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void tick()
    {
        if (!(this.world instanceof ServerWorld serverWorld)) return;

        if (this.current_health > 0.0f && this.max_health > 0.0f && this.current_health < this.max_health)
        {
            LocalDifficulty local = world.getLocalDifficulty(pos);
            float chance = HEAL_CHANCE * local.getClampedLocalDifficulty();
            if (world.getRandom().nextFloat() < chance) {
                this.current_health += Math.max(local.getClampedLocalDifficulty(), 0);

                if (this.current_health > this.max_health)
                    this.current_health = this.max_health;

                markDirty();

                // Spawn particles
                Random random = world.getRandom();
                double px = (double) this.pos.getX() + random.nextDouble();
                double py = (double) this.pos.getY() + random.nextDouble();
                double pz = (double) this.pos.getZ() + random.nextDouble();

                serverWorld.spawnParticles(ParticleTypes.HEART, px, py, pz, 3, 0, 0.1, 0, 0.15);
            }
        }
    }
}


