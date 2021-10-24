package com.feywild.feywild.world.structure.structures;

import com.feywild.feywild.config.WorldGenConfig;
import com.feywild.feywild.config.data.StructureData;
import com.feywild.feywild.entity.ModEntityTypes;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.List;

public class BeekeepStructure extends BaseStructure {

    private static final List<MobSpawnSettings.SpawnerData> STRUCTURE_CREATURES = ImmutableList.of(
            new MobSpawnSettings.SpawnerData(ModEntityTypes.beeKnight, 1, 1, 1)
    );

    @Override
    public StructureData getStructureData() {
        return WorldGenConfig.structures.bee_keep;
    }

    @Override
    public String getStructureId() {
        return "beekeep/start_pool";
    }

    @Override
    public int getSeedModifier() {
        return 345820124;
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getDefaultCreatureSpawnList() {
        return STRUCTURE_CREATURES;
    }
}
