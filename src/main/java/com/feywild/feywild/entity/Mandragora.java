package com.feywild.feywild.entity;

import com.feywild.feywild.FeywildMod;
import com.feywild.feywild.block.ModBlocks;
import com.feywild.feywild.entity.base.FeyBase;
import com.feywild.feywild.entity.base.GroundFeyBase;
import com.feywild.feywild.entity.goals.GoToTargetPositionGoal;
import com.feywild.feywild.entity.goals.SingGoal;
import com.feywild.feywild.network.ParticleSerializer;
import com.feywild.feywild.quest.Alignment;
import com.feywild.feywild.sound.ModSoundEvents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Mandragora extends GroundFeyBase implements IAnimatable {

    public static final EntityDataAccessor<Boolean> CASTING = SynchedEntityData.defineId(Mandragora.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Mandragora.class, EntityDataSerializers.INT);
    private final AnimationFactory factory = new AnimationFactory(this);

    public Mandragora(EntityType<? extends FeyBase> type, Level level) {
        super(type, Alignment.SPRING, level);
        this.noCulling = true;
        this.moveControl = new MoveControl(this);
        this.entityData.set(VARIANT, getRandom().nextInt(MandragoraVariant.values().length));
    }

    public static AttributeSupplier.Builder getDefaultAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, Attributes.MOVEMENT_SPEED.getDefaultValue())
                .add(Attributes.MAX_HEALTH, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.LUCK, 0.2);
    }

    @Nullable
    @Override
    public SimpleParticleType getParticle() {
        return null;
    }

    public MandragoraVariant getVariation() {
        return MandragoraVariant.values()[this.entityData.get(VARIANT)];
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(50, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new MoveTowardsTargetGoal(this, 0.2f, 4));
        this.goalSelector.addGoal(2, new GoToTargetPositionGoal(this, this::getCurrentPointOfInterest, 5, 0.2f));
        this.goalSelector.addGoal(10, new TemptGoal(this, 1.25, Ingredient.of(Items.COOKIE), false));
        this.goalSelector.addGoal(20, new SingGoal(this));
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("variant", this.entityData.get(VARIANT));
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("variant")) {
            this.entityData.set(VARIANT, nbt.getInt("variant"));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CASTING, false);
        this.entityData.define(VARIANT, VARIANT.getId());
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, true);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, true);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, true);
    }

    public boolean isCasting() {
        return this.entityData.get(CASTING);
    }

    public void setCasting(boolean casting) {
        this.entityData.set(CASTING, casting);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @Nonnull DamageSource source) {
        return false;
    }

    @Override
    protected int getExperienceReward(@Nonnull Player player) {
        return 0;
    }

    @Override
    public boolean canBeLeashed(@Nonnull Player player) {
        return false;
    }

    @Override
    protected boolean canRide(@Nonnull Entity entityIn) {
        return false;
    }

    @Override
    public float getVoicePitch() {
        return 1;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return ModSoundEvents.mandragoraHurt;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.mandragoraDeath;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.random.nextBoolean() ? ModSoundEvents.mandragoraAmbience : null;
    }

    @Nonnull
    @Override
    public InteractionResult interactAt(@Nonnull Player player, @Nonnull Vec3 hitVec, @Nonnull InteractionHand hand) {
        if (player.getItemInHand(hand).getItem() == Items.COOKIE && (this.getLastHurtByMob() == null || !this.getLastHurtByMob().isAlive())) {
            if (!level.isClientSide) {
                this.heal(4);
                if (!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }
                FeywildMod.getNetwork().sendParticles(this.level, ParticleSerializer.Type.FEY_HEART, this.getX(), this.getY(), this.getZ());
                player.swing(hand, true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else if (player.getItemInHand(hand).getItem() == ModBlocks.mandrakeCrop.getSeed()) {
            if (!level.isClientSide) {
                Mandragora entity = ModEntityTypes.mandragora.create(level);
                if (entity != null) {
                    entity.setCurrentTargetPos(this.getCurrentPointOfInterest());
                    entity.setPos(position().x, position().y, position().z);
                    entity.setOwner(getOwner());
                    level.addFreshEntity(entity);
                    if (!player.isCreative())
                        player.getItemInHand(hand).shrink(1);
                    remove(Entity.RemovalReason.DISCARDED);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.interactAt(player, hitVec, hand);
    }

    private <E extends IAnimatable> PlayState animationPredicate(AnimationEvent<E> event) {
        if (this.isCasting() && !(this.dead || this.isDeadOrDying())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mandragora.sing", false));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mandragora.walk", true));
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mandragora.idle", true));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::animationPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public enum MandragoraVariant {
        MELON, ONION, POTATO, PUMPKIN, TOMATO
    }
}
