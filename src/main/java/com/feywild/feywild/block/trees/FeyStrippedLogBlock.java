package com.feywild.feywild.block.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;

public class FeyStrippedLogBlock extends RotatedPillarBlock {

    private final FeyStrippedWoodBlock feyStrippedWood;

    public FeyStrippedLogBlock(FeyStrippedWoodBlock feyStrippedWood, Properties properties) {
        super(properties);
        this.feyStrippedWood = feyStrippedWood;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction) {
        BlockState toReturn = null;

        if (toolAction == ToolActions.AXE_STRIP) {

            toReturn = feyStrippedWood.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }

        return toReturn;
    }

}
