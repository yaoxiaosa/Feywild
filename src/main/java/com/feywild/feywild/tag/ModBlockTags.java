package com.feywild.feywild.tag;

import com.feywild.feywild.FeywildMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {

    public static final Tag.Named<Block> FEY_LOGS = BlockTags.bind(new ResourceLocation(FeywildMod.getInstance().modid, "fey_logs").toString());

}
