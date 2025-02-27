package com.feywild.feywild.block;

import com.feywild.feywild.entity.MandragoraEntity;
import com.feywild.feywild.entity.ModEntityTypes;
import com.feywild.feywild.item.ModItems;
import com.feywild.feywild.quest.Alignment;
import com.feywild.feywild.quest.player.QuestData;
import com.feywild.feywild.quest.task.SpecialTask;
import com.feywild.feywild.quest.util.SpecialTaskAction;
import com.feywild.feywild.sound.ModSoundEvents;
import com.google.common.collect.ImmutableMap;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.block.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public class MandrakeCrop extends CropsBlock implements Registerable {

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(0, 0, 0, 16, 12, 16),
            Block.box(0, 0, 0, 16, 14, 16),
            Block.box(0, 0, 0, 16, 16, 16)
    };

    private final BlockItem seed;

    public MandrakeCrop(ModX mod) {
        super(AbstractBlock.Properties.copy(Blocks.WHEAT));
        Item.Properties properties = mod.tab == null ? new Item.Properties() : new Item.Properties().tab(mod.tab);
        this.seed = new BlockItem(this, properties);
    }

    @Override
    public Map<String, Object> getNamedAdditionalRegisters() {
        return ImmutableMap.of(
                "seed", this.seed
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        defer.accept(() -> RenderTypeLookup.setRenderLayer(this, RenderType.cutout()));
    }

    @Nonnull
    @Override
    protected IItemProvider getBaseSeedId() {
        return this.seed;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPES[state.getValue(this.getAgeProperty())];
    }

    public BlockItem getSeed() {
        return this.seed;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        if (player.getItemInHand(hand).getItem() == ModItems.magicalHoneyCookie && state.getValue(this.getAgeProperty()) == 7) {
            if (!world.isClientSide && QuestData.get((ServerPlayerEntity) player).getAlignment() == Alignment.SPRING) {
                MandragoraEntity entity = ModEntityTypes.mandragora.create(world);

                if (entity != null) {
                    entity.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                    entity.setCurrentTargetPos(pos);
                    world.addFreshEntity(entity);
                    entity.playSound(SoundEvents.FOX_EAT, 1, 1);
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
                    QuestData.get((ServerPlayerEntity) player).checkComplete(SpecialTask.INSTANCE, SpecialTaskAction.SUMMON_MANDRAGORA);
                }
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        } else {
            world.playSound(player, pos, ModSoundEvents.mandrakeScream, SoundCategory.BLOCKS, 1.0f, 0.8f);
            return super.use(state, world, pos, player, hand, hit);
        }
    }
}
