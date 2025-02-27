package com.feywild.feywild.block.trees;

import com.feywild.feywild.config.ClientConfig;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockBase;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Consumer;

public class FeyLeavesBlock extends BlockBase implements Registerable, IForgeShearable {

    public static final int MAX_DISTANCE = 15;
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, MAX_DISTANCE);
    
    public static final int MAX_PARTICLE_DISTANCE = 48;

    private final int chance;
    private final BasicParticleType particle;
    
    public FeyLeavesBlock(ModX mod, int chance, BasicParticleType particle) {
        super(mod, AbstractBlock.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS)
                .harvestTool(ToolType.HOE).noOcclusion().isValidSpawn((s, r, p, t) -> false).isSuffocating((s, r, p) -> false)
                .isViewBlocking((s, r, p) -> false));
        this.chance = chance;
        this.particle = particle;

        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, 0).setValue(BlockStateProperties.PERSISTENT, false));
    }
    
    @Override
    public void registerCommon(ResourceLocation id, Consumer<Runnable> defer) {
        defer.accept(() -> ComposterBlock.add(0.4f, this));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        defer.accept(() -> RenderTypeLookup.setRenderLayer(this, RenderType.cutout()));
    }


    protected static BlockState updateDistance(BlockState state, IWorld worldIn, BlockPos pos) {
        int distance = MAX_DISTANCE;
        BlockPos.Mutable current = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            if (distance > 1) {
                current.setWithOffset(pos, direction);
                distance = Math.min(distance, getDistance(worldIn.getBlockState(current)) + 1);
            }
        }
        return state.setValue(DISTANCE, distance);
    }

    private static int getDistance(BlockState neighbor) {
        if (BlockTags.LOGS.contains(neighbor.getBlock())) {
            return 0;
        } else if (neighbor.getBlock() instanceof FeyLeavesBlock) {
            return neighbor.getValue(DISTANCE);
        } else {
            return MAX_DISTANCE;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, BlockStateProperties.PERSISTENT);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return updateDistance(this.defaultBlockState().setValue(BlockStateProperties.PERSISTENT, true), context.getLevel(), context.getClickedPos());
    }
    
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(DISTANCE) == MAX_DISTANCE && !state.getValue(BlockStateProperties.PERSISTENT);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(@Nonnull BlockState state, @Nonnull ServerWorld world, @Nonnull BlockPos pos, @Nonnull Random random) {
        updateDistance(state, world, pos);
        if (!state.getValue(BlockStateProperties.PERSISTENT) && state.getValue(DISTANCE) == MAX_DISTANCE) {
            dropResources(state, world, pos);
            world.removeBlock(pos, false);
        }
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void tick(@Nonnull BlockState state, ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        worldIn.setBlock(pos, updateDistance(state, worldIn, pos), 3);
    }
    
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld world, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        int distance = getDistance(facingState) + 1;
        if (distance != 1 || state.getValue(DISTANCE) != distance) {
            world.getBlockTicks().scheduleTick(pos, this, 1);
        }
        return state;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (ClientConfig.tree_particles) {
            // Don't add particles if the blocks are far away
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.position().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < MAX_PARTICLE_DISTANCE * MAX_PARTICLE_DISTANCE) {
                animateLeaves(state, world, pos, rand);
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void animateLeaves(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (rand.nextInt(15) == 0 && world.isRainingAt(pos.above())) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = world.getBlockState(blockpos);
            if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(world, blockpos, Direction.UP)) {
                double x = pos.getX() + rand.nextDouble();
                double y = pos.getY() - 0.05;
                double z = pos.getZ() + rand.nextDouble();
                world.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0, 0, 0);
            }
        }
        if (rand.nextInt(chance) == 1 && world.isEmptyBlock(pos.below())) {
            world.addParticle(particle, pos.getX() + rand.nextDouble(), pos.getY(),pos.getZ()+ rand.nextDouble(), 1, -0.1, 0 );
        }
    }
}
