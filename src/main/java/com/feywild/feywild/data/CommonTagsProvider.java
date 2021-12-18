package com.feywild.feywild.data;

import com.feywild.feywild.block.ModBlocks;
import com.feywild.feywild.block.ModTrees;
import com.feywild.feywild.block.flower.GiantFlowerBlock;
import com.feywild.feywild.block.trees.*;
import com.feywild.feywild.item.ModItems;
import com.feywild.feywild.item.Schematics;
import com.feywild.feywild.tag.ModBlockTags;
import com.feywild.feywild.tag.ModItemTags;
import io.github.noeppi_noeppi.libx.annotation.data.Datagen;
import io.github.noeppi_noeppi.libx.data.provider.CommonTagsProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

@Datagen
public class CommonTagsProvider extends CommonTagsProviderBase {

    public CommonTagsProvider(ModX mod, DataGenerator generator, ExistingFileHelper fileHelper) {
        super(mod, generator, fileHelper);
    }

    @Override
    public void setup() {
        this.item(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(ModItems.feywildMusicDisc);
        this.item(ModItemTags.SCHEMATICS).add(ModItems.schematicsElementalRuneCrafting);
        this.item(ModItemTags.SCHEMATICS).add(ModItems.schematicsSeasonalRuneCrafting);
        this.item(ModItemTags.SCHEMATICS).add(ModItems.schematicsDeadlyRuneCrafting);
        this.item(ModItemTags.SCHEMATICS).add(ModItems.schematicsYggdrasilRuneCrafting);

        this.item(ModItemTags.YGGDRASIL_BOOKS).add(ModItems.schematicsYggdrasilRuneCrafting);
        this.item(ModItemTags.DEADLY_BOOKS).add(ModItems.schematicsDeadlyRuneCrafting);
        this.item(ModItemTags.DEADLY_BOOKS).addTag(ModItemTags.YGGDRASIL_BOOKS);
        this.item(ModItemTags.SEASONAL_BOOKS).add(ModItems.schematicsSeasonalRuneCrafting);
        this.item(ModItemTags.SEASONAL_BOOKS).addTag(ModItemTags.DEADLY_BOOKS);
        this.item(ModItemTags.ELEMENTAL_BOOKS).add(ModItems.schematicsElementalRuneCrafting);
        this.item(ModItemTags.ELEMENTAL_BOOKS).addTag(ModItemTags.SEASONAL_BOOKS);

        this.block(BlockTags.LOGS).addTag(ModBlockTags.FEY_LOGS);
        this.block(BlockTags.LOGS_THAT_BURN).addTag(ModBlockTags.FEY_LOGS);

        this.block(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.feyGemBlock,
                ModBlocks.feyGemBlockLivingrock,
                ModBlocks.dwarvenAnvil);
        this.block(BlockTags.NEEDS_IRON_TOOL).add(
                ModBlocks.feyGemBlock,
                ModBlocks.feyGemBlockLivingrock,
                ModBlocks.dwarvenAnvil);

        this.copyBlock(BlockTags.LOGS, ItemTags.LOGS);
        this.copyBlock(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        this.copyBlock(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        this.copyBlock(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copyBlock(ModBlockTags.FEY_LOGS, ModItemTags.FEY_LOGS);

        this.block(ModBlockTags.AUTUMN_LOGS).add(
                ModTrees.autumnTree.getLogBlock(),
                ModTrees.autumnTree.getStrippedLogBlock(),
                ModTrees.autumnTree.getWoodBlock(),
                ModTrees.autumnTree.getStrippedWoodBlock());
        this.copyBlock(ModBlockTags.AUTUMN_LOGS, ModItemTags.AUTUMN_LOGS);

        this.block(ModBlockTags.SPRING_LOGS).add(
                ModTrees.springTree.getLogBlock(),
                ModTrees.springTree.getStrippedLogBlock(),
                ModTrees.springTree.getWoodBlock(),
                ModTrees.springTree.getStrippedWoodBlock());
        this.copyBlock(ModBlockTags.SPRING_LOGS, ModItemTags.SPRING_LOGS);

        this.block(ModBlockTags.SUMMER_LOGS).add(
                ModTrees.summerTree.getLogBlock(),
                ModTrees.summerTree.getStrippedLogBlock(),
                ModTrees.summerTree.getWoodBlock(),
                ModTrees.summerTree.getStrippedWoodBlock());
        this.copyBlock(ModBlockTags.SUMMER_LOGS, ModItemTags.SUMMER_LOGS);

        this.block(ModBlockTags.WINTER_LOGS).add(
                ModTrees.winterTree.getLogBlock(),
                ModTrees.winterTree.getStrippedLogBlock(),
                ModTrees.winterTree.getWoodBlock(),
                ModTrees.winterTree.getStrippedWoodBlock());
        this.copyBlock(ModBlockTags.WINTER_LOGS, ModItemTags.WINTER_LOGS);

        this.block(BlockTags.PLANKS).add(
                ModTrees.autumnTree.getPlankBlock(),
                ModTrees.springTree.getPlankBlock(),
                ModTrees.summerTree.getPlankBlock(),
                ModTrees.winterTree.getPlankBlock());
        this.copyBlock(BlockTags.PLANKS, ItemTags.PLANKS);

        this.block(BlockTags.WOODEN_FENCES).add(
                ModTrees.autumnTree.getPlankBlock().getStairBlock(),
                ModTrees.springTree.getPlankBlock().getStairBlock(),
                ModTrees.summerTree.getPlankBlock().getStairBlock(),
                ModTrees.winterTree.getPlankBlock().getStairBlock());
        this.copyBlock(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);

        this.block(BlockTags.WOODEN_SLABS).add(
                ModTrees.autumnTree.getPlankBlock().getSlabBlock(),
                ModTrees.springTree.getPlankBlock().getSlabBlock(),
                ModTrees.summerTree.getPlankBlock().getSlabBlock(),
                ModTrees.winterTree.getPlankBlock().getSlabBlock());
        this.copyBlock(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);

        this.block(BlockTags.WOODEN_STAIRS).add(
                ModTrees.autumnTree.getPlankBlock().getStairBlock(),
                ModTrees.springTree.getPlankBlock().getStairBlock(),
                ModTrees.summerTree.getPlankBlock().getStairBlock(),
                ModTrees.winterTree.getPlankBlock().getStairBlock());
        this.copyBlock(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
    }

    @Override
    public void defaultItemTags(Item item) {
        if (item instanceof Schematics) {
            this.item(ModItemTags.SCHEMATICS).add(item);
        }
    }

    @Override
    public void defaultBlockTags(Block block) {
        if (block instanceof FeyLogBlock || block instanceof FeyWoodBlock || block instanceof FeyStrippedLogBlock || block instanceof FeyStrippedWoodBlock) {
            this.block(ModBlockTags.FEY_LOGS).add(block);
        } else if (block instanceof FeyLeavesBlock) {
            this.block(BlockTags.LEAVES).add(block);
            this.block(BlockTags.MINEABLE_WITH_HOE).add(block);
        } else if (block instanceof BaseSaplingBlock) {
            this.block(BlockTags.SAPLINGS).add(block);
        } else if (block instanceof GiantFlowerBlock) {
            this.block(BlockTags.TALL_FLOWERS).add(block);
            this.block(BlockTags.MINEABLE_WITH_AXE).add(block);
        }
    }
}
