package com.feywild.feywild.world.biome;

import com.feywild.feywild.FeywildMod;
import com.feywild.feywild.world.biome.biomes.*;
import com.feywild.feywild.world.structure.ModStructures;
import mythicbotany.alfheim.AlfheimBiomeManager;
import mythicbotany.alfheim.AlfheimBiomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

// No @RegisterClass as this may not be loaded if MythicBotany is
// not installed
public class ModAlfheimBiomes {

    public static final BiomeEnvironment ALFHEIM = new BiomeEnvironment() {

        @Override
        public Biome.Builder init() {
            return AlfheimBiomes.alfheimBiome();
        }

        @Override
        public BiomeAmbience.Builder defaultAmbience() {
            return AlfheimBiomes.alfheimAmbience();
        }

        @Override
        public MobSpawnInfo.Builder defaultSpawns() {
            return AlfheimBiomes.alfheimMobs();
        }

        @Override
        public BiomeGenerationSettings.Builder defaultGeneration(ConfiguredSurfaceBuilder<?> surface) {
            return AlfheimBiomes.alfheimGen(surface);
        }
        
        @Override
        public void postProcess(BiomeAmbience.Builder builder, BiomeType biome) {
            builder.waterColor(0x43d5ee);
            builder.waterFogColor(0x41f33);
            builder.fogColor(0xc0d8ff);
        }
    };
    
    // Add 4 blank alfheim biomes that are then customised by BiomeLoader
    public static final Biome alfheimSpring = BiomeFactory.create(ALFHEIM, SpringBiome.INSTANCE);
    public static final Biome alfheimSummer = BiomeFactory.create(ALFHEIM, SummerBiome.INSTANCE);
    public static final Biome alfheimAutumn = BiomeFactory.create(ALFHEIM, AutumnBiome.INSTANCE);
    public static final Biome alfheimWinter = BiomeFactory.create(ALFHEIM, WinterBiome.INSTANCE);

    public static void register() {
        FeywildMod.getInstance().register("alfheim_spring", alfheimSpring);
        FeywildMod.getInstance().register("alfheim_summer", alfheimSummer);
        FeywildMod.getInstance().register("alfheim_autumn", alfheimAutumn);
        FeywildMod.getInstance().register("alfheim_winter", alfheimWinter);
    }

    public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AlfheimBiomeManager.addUncommonBiome(alfheimSpring.getRegistryName());
            AlfheimBiomeManager.addUncommonBiome(alfheimSummer.getRegistryName());
            AlfheimBiomeManager.addUncommonBiome(alfheimAutumn.getRegistryName());
            AlfheimBiomeManager.addUncommonBiome(alfheimWinter.getRegistryName());
            
            AlfheimBiomeManager.addStructure(ModStructures.springWorldTree, ModStructures.springWorldTree.getSettings());
            AlfheimBiomeManager.addStructure(ModStructures.summerWorldTree, ModStructures.summerWorldTree.getSettings());
            AlfheimBiomeManager.addStructure(ModStructures.autumnWorldTree, ModStructures.autumnWorldTree.getSettings());
            AlfheimBiomeManager.addStructure(ModStructures.winterWorldTree, ModStructures.winterWorldTree.getSettings());
            AlfheimBiomeManager.addStructure(ModStructures.beekeep, ModStructures.beekeep.getSettings());
        });
    }
}
