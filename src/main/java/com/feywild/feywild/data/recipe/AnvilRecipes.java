package com.feywild.feywild.data.recipe;

import com.feywild.feywild.item.ModItems;
import com.feywild.feywild.recipes.ModRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.noeppi_noeppi.libx.data.CraftingHelper2;
import io.github.noeppi_noeppi.libx.data.provider.recipe.AnyRecipeProvider;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnvilRecipes extends AnyRecipeProvider {

    public AnvilRecipes(ModX mod, DataGenerator generator) {
        super(mod, generator);
    }

    @Nonnull
    @Override
    public String getName() {
        return this.mod.modid + " fey anvil recipes";
    }

    @Override
    protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        this.gemTransmutation(consumer, ModItems.lesserFeyGem, ModItems.greaterFeyGem, 50);
        this.gemTransmutation(consumer, ModItems.greaterFeyGem, ModItems.shinyFeyGem, 100);
        this.gemTransmutation(consumer, ModItems.shinyFeyGem, ModItems.brilliantFeyGem, 150);

        /* EXAMPLE
        this.anvil(ModBlocks.feyAltar)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(ModItems.lesserFeyGem)
                .requires(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .requires(ModItems.brilliantFeyGem)
                .schematics(ModItems.schematicsFeyAltar)
                .mana(1000)
                .build(consumer); */
    }

    private void gemTransmutation(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider result, int mana) {
        this.anvil(result, 2)
                .requires(input)
                .requires(input)
                .requires(input)
                .requires(input)
                .requires(input)
                .schematics(ModItems.schematicsGemTransmutation)
                .mana(mana)
                .build(consumer);
    }

    private AnvilRecipeBuilder anvil(IItemProvider result) {
        return this.anvil(new ItemStack(result));
    }

    private AnvilRecipeBuilder anvil(IItemProvider result, int amount) {
        return this.anvil(new ItemStack(result, amount));
    }

    private AnvilRecipeBuilder anvil(ItemStack result) {
        return new AnvilRecipeBuilder(result);
    }

    private class AnvilRecipeBuilder {

        private final ItemStack result;
        private final List<Ingredient> inputs;
        @Nullable
        private Ingredient schematics;
        private int mana = -1;

        public AnvilRecipeBuilder(ItemStack result) {
            this.result = result;
            this.inputs = new ArrayList<>();
        }

        public AnvilRecipeBuilder requires(IItemProvider item) {
            return this.requires(Ingredient.of(item));
        }

        public AnvilRecipeBuilder requires(ITag<Item> item) {
            return this.requires(Ingredient.of(item));
        }

        public AnvilRecipeBuilder requires(Ingredient item) {
            this.inputs.add(item);
            return this;
        }

        public AnvilRecipeBuilder schematics(IItemProvider item) {
            return this.schematics(Ingredient.of(item));
        }

        public AnvilRecipeBuilder schematics(ITag<Item> item) {
            return this.schematics(Ingredient.of(item));
        }

        public AnvilRecipeBuilder schematics(Ingredient item) {
            if (this.schematics != null)
                throw new IllegalStateException("Can't build dwarven anvil recipe with multiple schematics");
            this.schematics = item;
            return this;
        }

        public AnvilRecipeBuilder mana(int mana) {
            if (this.mana >= 0) throw new IllegalStateException("Mana can only set once per dwarven anvil recipe.");
            this.mana = mana;
            return this;
        }

        public void build(Consumer<IFinishedRecipe> consumer) {
            this.build(consumer, AnvilRecipes.this.loc(this.result.getItem(), "dwarven_anvil"));
        }

        public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
            if (this.inputs.isEmpty())
                throw new IllegalStateException("Can't build dwarven anvil recipe without inputs: " + id);
            if (this.inputs.size() > 5)
                throw new IllegalStateException("Can't build dwarven anvil recipe with more than 5 inputs: " + id);
            if (this.mana < 0) throw new IllegalStateException("mana not set for dwarven anvil recipe: " + id);
            consumer.accept(new IFinishedRecipe() {

                @Override
                public void serializeRecipeData(@Nonnull JsonObject json) {
                    json.addProperty("mana", AnvilRecipeBuilder.this.mana);
                    json.add("output", CraftingHelper2.serializeItemStack(AnvilRecipeBuilder.this.result, true));
                    if (AnvilRecipeBuilder.this.schematics != null) {
                        json.add("schematics", AnvilRecipeBuilder.this.schematics.toJson());
                    }
                    JsonArray inputList = new JsonArray();
                    AnvilRecipeBuilder.this.inputs.forEach(i -> inputList.add(i.toJson()));
                    json.add("ingredients", inputList);
                }

                @Nonnull
                @Override
                public ResourceLocation getId() {
                    return id;
                }

                @Nonnull
                @Override
                public IRecipeSerializer<?> getType() {
                    return ModRecipeTypes.DWARVEN_ANVIL_SERIALIZER;
                }

                @Nullable
                @Override
                public JsonObject serializeAdvancement() {
                    return null;
                }

                @Nullable
                @Override
                public ResourceLocation getAdvancementId() {
                    return null;
                }
            });
        }
    }
}
