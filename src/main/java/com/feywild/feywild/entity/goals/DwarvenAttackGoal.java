package com.feywild.feywild.entity.goals;

import com.feywild.feywild.entity.DwarfBlacksmithEntity;
import com.feywild.feywild.sound.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class DwarvenAttackGoal extends Goal {

    protected final DwarfBlacksmithEntity entity;
    protected LivingEntity target;
    protected boolean sendShock = false;
    protected int ticksLeft = 0;

    public DwarvenAttackGoal(DwarfBlacksmithEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick() {
        if (this.entity.getLastHurtByMob() instanceof MonsterEntity) {
            this.target = this.entity.getLastHurtByMob();
            this.ticksLeft--;
            if (this.ticksLeft == 0) {
                this.reset();
            } else if (this.ticksLeft == 10) {
                this.sendShock = this.attackTarget();
                this.entity.playSound(ModSoundEvents.dwarfAttack, 1, 1.2f);
                this.entity.setState(DwarfBlacksmithEntity.State.IDLE);
            } else if (this.ticksLeft == 30) {
                this.entity.setState(DwarfBlacksmithEntity.State.ATTACKING);
                this.entity.getNavigation().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 0.5);
            } else if (this.ticksLeft <= 30) {
                this.entity.lookAt(EntityAnchorArgument.Type.EYES, this.target.position());
                if (this.sendShock) {
                    switch (this.ticksLeft) {
                        case 4:
                        case 8:
                            this.summonShockWave(true);
                            break;
                        case 6:
                            this.summonShockWave(false);
                            break;
                    }
                }
            }
        } else {
            this.reset();
        }
    }

    @Override
    public void start() {
        this.ticksLeft = 70;
    }

    protected boolean attackTarget() {
        if (this.entity.getRandom().nextDouble() > 0.6) {
            this.target.hurt(DamageSource.mobAttack(this.entity), 15);
            return false;
        } else {
            this.target.hurt(DamageSource.mobAttack(this.entity), 20);
            return true;
        }
    }

    private void summonShockWave(boolean stage) {
        BlockPos centerPos = this.entity.blockPosition().below();

        int size = 2;
        List<FallingBlockEntity> entityList = new ArrayList<>();

        for (int xd = 0; xd <= size; xd++) {
            for (int zd = 0; zd <= size; zd++) {
                int dist = Math.abs(xd) + Math.abs(zd);
                //noinspection deprecation
                if (dist <= size && !this.entity.level.getBlockState(centerPos.offset(xd, -1, zd)).isAir() && this.entity.level.getBlockState(new BlockPos(centerPos.offset(xd, 0, zd))).isAir() && (xd != 0 || zd != 0)) {
                    if (stage) {
                        this.waveBlock(entityList, centerPos.offset(xd, 0, zd));
                        this.waveBlock(entityList, centerPos.offset(-xd, 0, -zd));
                    } else {
                        this.waveBlock(entityList, centerPos.offset(xd, 0, -zd));
                        this.waveBlock(entityList, centerPos.offset(-xd, 0, zd));
                    }
                }
            }
        }

        entityList.forEach(block -> {
            this.entity.playSound(ModSoundEvents.dwarfAttack, 1, 1);
            block.setDeltaMovement(0, 0.3d, 0);
            block.setHurtsEntities(true);
            this.entity.level.addFreshEntity(block);
        });
    }

    private void waveBlock(List<FallingBlockEntity> entityList, BlockPos pos) {
        BlockState state = this.entity.level.getBlockState(pos);
        if (!state.hasTileEntity() && state.getDestroySpeed(this.entity.level, pos) >= 0) {
            // No blocks with tile entities and no unbreakable blocks.
            entityList.add(new FallingBlockEntity(this.entity.level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state));
        }
    }

    protected void reset() {
        this.entity.setState(DwarfBlacksmithEntity.State.IDLE);
        this.target = null;
        this.sendShock = false;
        this.ticksLeft = -1;
    }

    @Override
    public boolean canContinueToUse() {
        return this.ticksLeft > 0 && this.entity.getLastHurtByMob() instanceof MobEntity && !this.entity.getLastHurtByMob().isInvulnerable();
    }

    @Override
    public boolean canUse() {
        return this.entity.getLastHurtByMob() instanceof MobEntity && !this.entity.getLastHurtByMob().isInvulnerable() && this.entity.canSee(this.entity.getLastHurtByMob());
    }
}