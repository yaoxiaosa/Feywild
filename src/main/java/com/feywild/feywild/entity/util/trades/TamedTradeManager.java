package com.feywild.feywild.entity.util.trades;


import com.feywild.feywild.item.ModItems;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.feywild.feywild.entity.util.trades.DwarvenTrades.*;

public class TamedTradeManager implements IFutureReloadListener {

    private static final Gson GSON = new GsonBuilder().create();
    private static TamedTradeManager instance;
    private static String tamedPaths[] = {"feywild_trades/tamed/food", "feywild_trades/tamed/loot"};
    private static String untamedPaths[] = {"feywild_trades/untamed"};

    public static TamedTradeManager instance()
    {
        if(instance == null)
        {
            instance = new TamedTradeManager();
        }
        return instance;
    }

    @Override
    public CompletableFuture<Void> reload(IStage iStage, IResourceManager iResourceManager, IProfiler iProfiler, IProfiler iProfiler1, Executor executor, Executor executor1) {
        return CompletableFuture.allOf(CompletableFuture.runAsync(() ->
        {
            //For tamed
            DwarvenTrades.TamedSerializer serializer = new DwarvenTrades.TamedSerializer();
            for (String path : tamedPaths) {
                List<ResourceLocation> resources = (List<ResourceLocation>) iResourceManager.listResources(path, s -> s.endsWith(".json"));

                resources.forEach(resourceLocation -> {

                    try (InputStream stream = iResourceManager.getResource(resourceLocation).getInputStream(); Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                        if(resourceLocation.getPath().contains("common")) {
                            List<TradeData> common = serializer.deserialize(Objects.requireNonNull(JSONUtils.fromJson(GSON, reader, JsonObject.class)), 0);
                            if(!common.isEmpty()) {
                                if (path.endsWith("loot"))
                                    DwarvenTrades.commonLoot.addAll(common);
                                else if (path.endsWith("food"))
                                    DwarvenTrades.commonFood.addAll(common);
                            }
                        }
                        if(resourceLocation.getPath().contains("legendary")) {
                            List<TradeData> legendary = serializer.deserialize(Objects.requireNonNull(JSONUtils.fromJson(GSON, reader, JsonObject.class)), 1);
                            if(!legendary.isEmpty()){
                                if(path.endsWith("loot"))
                                    DwarvenTrades.legendaryLoot.addAll(legendary);
                                else if(path.endsWith("food"))
                                    DwarvenTrades.legendaryFood.addAll(legendary);
                            }
                        }


                    } catch (IOException e) {
                        System.out.print("You are not abiding by the rules of the feywild! (Tamed trade setup is wrong)");
                        e.printStackTrace();
                    }
                });
            }

            //For untamed
            //Ancient's note : Keep an eye on this if more level are needed though I doubt that
            DwarvenTrades.UntamedSerializer untamedSerializer =  new DwarvenTrades.UntamedSerializer();

            for (String path : untamedPaths) {
                List<ResourceLocation> resources = (List<ResourceLocation>) iResourceManager.listResources(path, s -> s.endsWith(".json"));

                resources.forEach(resourceLocation -> {

                    try (InputStream stream = iResourceManager.getResource(resourceLocation).getInputStream(); Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                        List<DwarvenTrades.SimplyTrade> trades = untamedSerializer.deserialize(Objects.requireNonNull(JSONUtils.fromJson(GSON, reader, JsonObject.class)));
                        int level = Integer.parseInt(String.valueOf(resourceLocation.getPath().charAt(resourceLocation.getPath().length() - 6)));
                            switch (level){
                                case 1 :
                                    tamedLevel1.addAll(trades);
                                    break;
                                case 2:
                                    tamedLevel2.addAll(trades);
                                    break;
                                case 3:
                                    tamedLevel3.addAll(trades);
                                    break;
                                default:
                                    //Do nothing
                                    break;
                            }
                    } catch (IOException e) {
                        System.out.print("You are not abiding by the rules of the feywild! (Untamed trade setup is wrong)");
                        e.printStackTrace();
                    }
                });
                DWARVEN_TRADES = toIntMap(ImmutableMap.of(
                        1,getTrades(tamedLevel1),2, getTrades(tamedLevel2),3, getTrades(Collections.singletonList(new SimplyTrade(new ItemStack(ModItems.GREATER_FEY_GEM.get(), 5), new ItemStack(ModItems.SUMMONING_SCROLL_DWARF_BLACKSMITH.get(), 2), 1, 1, 10)
                        ))));



                DWARVEN_BLACKSMITH_TRADES = toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{
                                new DwarvenTrades.RandomCommonFoodItemsForRandomCommonOreItemsTrade(),
                                new DwarvenTrades.RandomCommonFoodItemsForRandomCommonOreItemsTrade(),
                                new DwarvenTrades.RandomCommonFoodItemsForRandomCommonOreItemsTrade(),
                                new DwarvenTrades.RandomLegendaryFoodItemsForRandomLegendaryOreItemsTrade(),
                        },

                        2, getTrades(tamedLevel3),
                        3, getTrades(Collections.singletonList(
                                new SimplyTrade(new ItemStack(ModItems.LESSER_FEY_GEM.get(), 2), new ItemStack(ModItems.SCHEMATICS_GEM_TRANSMUTATION.get(), 1), 1, 1, 5)
                        ))));
            }

        },executor)).thenCompose(iStage::wait);
    }
}
