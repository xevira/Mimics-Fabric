package github.xevira.mimics.entity.mob;

import github.xevira.mimics.Mimics;
import github.xevira.mimics.Registration;
import github.xevira.mimics.block.MimicChestBlock;
import github.xevira.mimics.block.entity.MimicChestBlockEntity;
import github.xevira.mimics.util.MimicDamage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.EnumSet;

public class MimicEntity extends HostileEntity {
    public MimicEntity(EntityType<? extends MimicEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
        this.experiencePoints = 10;
        this.moveControl = new MimicMoveControl(this);
    }

    public static DefaultAttributeContainer.Builder createMimicAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 26)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);
    }

    @Override
    protected void initGoals() {
        // Normal goals
        // They should not swim
        // TODO: Add a fleeing goal that checks for someone wielding an axe?
        this.goalSelector.add(1, new MimicEntity.RevertToMimicChestGoal(this));
        this.goalSelector.add(3, new MimicEntity.RandomLookGoal(this));
        this.goalSelector.add(2, new MimicEntity.FaceTowardTargetGoal(this));
        this.goalSelector.add(5, new MimicEntity.MoveGoal(this));

        // Target Selection
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    // TODO: Maybe a particle like the slimes?

    protected void damage(LivingEntity target) {
        if (this.isAlive() && this.isInAttackRange(target) && this.canSee(target)) {
            DamageSource damageSource = this.getDamageSources().mobAttack(this);
            if (target.damage(damageSource, this.getDamageAmount())) {
                this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    EnchantmentHelper.onTargetDamaged(serverWorld, target, damageSource);
                }
            }
        }
    }

    protected boolean canAttack() {
        return this.canMoveVoluntarily();
    }

    protected float getDamageAmount() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        super.pushAwayFrom(entity);
        if (entity instanceof IronGolemEntity && this.canAttack()) {
            this.damage((LivingEntity)entity);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.canAttack()) {
            this.damage(player);
        }
    }

    @Override
    public void jump() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x, (double)this.getJumpVelocity(), vec3d.z);
        this.velocityDirty = true;
    }

    protected int getTicksUntilNextJump() {
        return this.random.nextInt(4) + 4;
    }

    public MimicDamage.DamageLevel getDamageLevel() {
        return MimicDamage.MIMIC.getMimicDamageLevel(this.getHealth() / this.getMaxHealth());
    }

    // Sounds

    static class FaceTowardTargetGoal extends Goal {
        private final MimicEntity mimic;
        private int ticksLeft;

        public FaceTowardTargetGoal(MimicEntity slime) {
            this.mimic = slime;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.mimic.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return this.mimic.canTarget(livingEntity) && this.mimic.getMoveControl() instanceof MimicMoveControl;
            }
        }

        @Override
        public void start() {
            this.ticksLeft = toGoalTicks(300);
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = this.mimic.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return this.mimic.canTarget(livingEntity) && --this.ticksLeft > 0;
            }
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.mimic.getTarget();
            if (livingEntity != null) {
                this.mimic.lookAtEntity(livingEntity, 10.0F, 10.0F);
            }

            if (this.mimic.getMoveControl() instanceof MimicEntity.MimicMoveControl moveControl) {
                moveControl.look(this.mimic.getYaw(), this.mimic.canAttack());
            }
        }
    }


    static class MimicMoveControl extends MoveControl {
        private float targetYaw;
        private int ticksUntilJump;
        private final MimicEntity mimic;
        private boolean jumpOften;

        public MimicMoveControl(MimicEntity mimic) {
            super(mimic);
            this.mimic = mimic;
            this.targetYaw = 180.0F * mimic.getYaw() / (float) Math.PI;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            this.speed = speed;
            this.state = MoveControl.State.MOVE_TO;
        }

        @Override
        public void tick() {
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), this.targetYaw, 90.0F));
            this.entity.headYaw = this.entity.getYaw();
            this.entity.bodyYaw = this.entity.getYaw();
            if (this.state != MoveControl.State.MOVE_TO) {
                this.entity.setForwardSpeed(0.0F);
            } else {
                this.state = MoveControl.State.WAIT;
                if (this.entity.isOnGround()) {
                    this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                    if (this.ticksUntilJump-- <= 0) {
                        this.ticksUntilJump = this.mimic.getTicksUntilNextJump();
                        if (this.jumpOften) {
                            this.ticksUntilJump /= 3;
                        }

                        this.mimic.getJumpControl().setActive();
                        //  TODO: Jumping Sound
                        //if (this.slime.makesJumpSound()) {
                        //    this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getJumpSoundPitch());
                        //}
                        //
                    } else {
                        this.mimic.sidewaysSpeed = 0.0F;
                        this.mimic.forwardSpeed = 0.0F;
                        this.entity.setMovementSpeed(0.0F);
                    }
                } else {
                    this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                }
            }
        }
    }

    static class MoveGoal extends Goal {
        private final MimicEntity mimic;

        public MoveGoal(MimicEntity mimic) {
            this.mimic = mimic;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !this.mimic.hasVehicle();
        }

        @Override
        public void tick() {
            if (this.mimic.getMoveControl() instanceof MimicEntity.MimicMoveControl moveControl) {
                moveControl.move(1.0);
            }
        }
    }

    static class MimicAttackGoal extends MeleeAttackGoal {
        private final MimicEntity mimic;
        private int ticks;


        public MimicAttackGoal(MimicEntity mimic, double speed, boolean pauseWhenMobIdle) {
            super(mimic, speed, pauseWhenMobIdle);
            this.mimic = mimic;
        }
        @Override
        public void start() {
            super.start();
            this.ticks = 0;
        }

        @Override
        public void stop() {
            super.stop();
            this.mimic.setAttacking(false);
        }

        @Override
        public void tick() {
            super.tick();
            this.ticks++;
            if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
                this.mimic.setAttacking(true);
            } else {
                this.mimic.setAttacking(false);
            }
        }
    }

    static class RevertToMimicChestGoal extends Goal
    {
        private static final long DELAY_UNTIL_REVERT = 15 * 20; // 15 seconds

        private final MimicEntity mimic;
        private long timeSinceLastAttack;
        private final Random random = Random.create();

        public RevertToMimicChestGoal(MimicEntity mimic)
        {
            this.mimic = mimic;
            this.timeSinceLastAttack = 0;
        }

        @Override
        public boolean canStart() {
            long l = this.mimic.getWorld().getTime();
            if (this.timeSinceLastAttack < 1 ||         // Hasn't started this yet at all.
                    !this.mimic.isAlive() ||            // Is dead
                    !mimic.canMoveVoluntarily() ||      // Can't move on it's own
                    mimic.isAttacking())                // Is currently trying to attack something.
            {
                this.timeSinceLastAttack = l;
                return false;
            }

            return (l - this.timeSinceLastAttack) >= DELAY_UNTIL_REVERT;
        }

        @Override
        public void tick() {
            super.tick();

            World world = this.mimic.getWorld();

            // 10% chance after it starts
            if (this.random.nextInt(10) > 0) return;

            BlockPos pos = this.mimic.getBlockPos();

            // Only revert when sitting in AIR
            BlockState state = world.getBlockState(pos);
            if (!state.isAir()) return;

            Direction facing = Direction.fromRotation(this.mimic.getYaw());

            world.setBlockState(pos, Registration.MIMIC_CHEST.getDefaultState().with(MimicChestBlock.FACING, facing));

            // Store the current health *on* the chest block
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof MimicChestBlockEntity blockEntity)
            {
                blockEntity.setHealth(this.mimic.getHealth(), this.mimic.getMaxHealth());
            }

            // TODO: Make some noise?

            // Kill the mimic without allowing it to drop anything...
            this.mimic.discard();
        }
    }

    static class RandomLookGoal extends Goal {
        private final MimicEntity mimic;
        private float targetYaw;
        private int timer;

        public RandomLookGoal(MimicEntity mimic) {
            this.mimic = mimic;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.mimic.getTarget() == null
                    && (this.mimic.isOnGround() || this.mimic.isTouchingWater() || this.mimic.isInLava() || this.mimic.hasStatusEffect(StatusEffects.LEVITATION))
                    && this.mimic.getMoveControl() instanceof MimicEntity.MimicMoveControl;
        }

        @Override
        public void tick() {
            if (--this.timer <= 0) {
                this.timer = this.getTickCount(40 + this.mimic.getRandom().nextInt(60));
                this.targetYaw = (float)this.mimic.getRandom().nextInt(360);
            }

            if (this.mimic.getMoveControl() instanceof MimicEntity.MimicMoveControl moveControl) {
                moveControl.look(this.targetYaw, false);
            }
        }
    }

}
