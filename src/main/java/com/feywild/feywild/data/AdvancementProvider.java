package com.feywild.feywild.data;

import com.feywild.feywild.block.ModBlocks;
import com.feywild.feywild.block.ModTrees;
import com.feywild.feywild.entity.ModEntityTypes;
import com.feywild.feywild.item.ModItems;
import com.feywild.feywild.world.dimension.ModDimensions;
import io.github.noeppi_noeppi.libx.data.provider.AdvancementProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.VillagerTradeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

public class AdvancementProvider extends AdvancementProviderBase {

    public AdvancementProvider(ModX mod, DataGenerator generator) {
        super(mod, generator);
    }

    @Override
    public void setup() {
        this.root()
                .display(ModItems.feywildLexicon)
                .task(this.items(ModItems.feywildLexicon));

        // Don't ask about this
        try {
            ResourceLocation rl = new ResourceLocation(this.mod.modid, this.mod.modid + "/root");
            Field mapField = AdvancementProviderBase.class.getDeclaredField("advancements");
            mapField.setAccessible(true);
            //noinspection unchecked
            Map<ResourceLocation, Supplier<Advancement>> map = (Map<ResourceLocation, Supplier<Advancement>>) mapField.get(this);
            Supplier<Advancement> oldRoot = map.get(rl);
            Supplier<Advancement> newRoot = () -> {
                Advancement adv = oldRoot.get();
                if (adv.getDisplay() == null) throw new RuntimeException("Nul display on root");
                DisplayInfo display = new DisplayInfo(
                        adv.getDisplay().getIcon(), adv.getDisplay().getTitle(), adv.getDisplay().getDescription(),
                        new ResourceLocation("minecraft", "textures/gui/advancements/backgrounds/adventure.png"),
                        adv.getDisplay().getFrame(), adv.getDisplay().shouldShowToast(),
                        adv.getDisplay().shouldAnnounceChat(), adv.getDisplay().isHidden()
                );
                return new Advancement(adv.getId(), adv.getParent(), display, adv.getRewards(), adv.getCriteria(), adv.getRequirements());
            };
            map.put(rl, newRoot);
            Field rootSupplierField = AdvancementProviderBase.class.getDeclaredField("rootSupplier");
            rootSupplierField.setAccessible(true);
            rootSupplierField.set(this, newRoot);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        this.advancement("fey_dust")
                .display(ModItems.feyDust)
                .task(this.items(ModItems.feyDust), this.items(ModItems.lesserFeyGem));

        this.advancement("dwarf_trade").parent("fey_dust")
                .display(ModItems.lesserFeyGem)
                .task(new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, this.entity(ModEntityTypes.dwarfBlacksmith), this.stack(ModItems.lesserFeyGem)));

        this.advancement("dwarf_contract").parent("dwarf_trade")
                .display(ModItems.summoningScrollDwarfBlacksmith)
                .task(this.items(ModItems.summoningScrollDwarfBlacksmith));

        this.advancement("schematics_gem_transmutation").parent("dwarf_contract")
                .display(ModItems.schematicsGemTransmutation)
                .task(this.items(ModItems.schematicsGemTransmutation));

        this.advancement("fey_altar")
                .display(ModBlocks.feyAltar)
                .task(this.items(ModBlocks.feyAltar));

        this.advancement("fey_sapling")
                .display(ModTrees.springTree.getSapling())
                .task(
                        this.items(ModTrees.springTree.getSapling()),
                        this.items(ModTrees.summerTree.getSapling()),
                        this.items(ModTrees.autumnTree.getSapling()),
                        this.items(ModTrees.winterTree.getSapling())
                );

        this.advancement("summon_spring").parent("fey_altar")
                .display(ModItems.summoningScrollSpringPixie)
                .task(this.items(ModItems.summoningScrollSpringPixie));

        this.advancement("summon_summer").parent("fey_altar")
                .display(ModItems.summoningScrollSummerPixie)
                .task(this.items(ModItems.summoningScrollSummerPixie));

        this.advancement("summon_autumn").parent("fey_altar")
                .display(ModItems.summoningScrollAutumnPixie)
                .task(this.items(ModItems.summoningScrollAutumnPixie));

        this.advancement("summon_winter").parent("fey_altar")
                .display(ModItems.summoningScrollWinterPixie)
                .task(this.items(ModItems.summoningScrollWinterPixie));

        this.advancement("traveling_stone")
                .display(ModItems.inactiveMarketRuneStone)
                .task(this.items(ModItems.inactiveMarketRuneStone), this.items(ModItems.marketRuneStone));

        this.advancement("dwarven_market").parent("traveling_stone")
                .display(ModItems.marketRuneStone)
                .task(new ChangeDimensionTrigger.Instance(this.entity(EntityType.PLAYER), World.OVERWORLD, ModDimensions.MARKET_PLACE_DIMENSION));

        this.advancement("dwarf_trades").parent("dwarven_market")
                .display(ModItems.marketRuneStone)
                .task(
                        new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, this.entity(ModEntityTypes.dwarfBaker), ItemPredicate.ANY),
                        new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, this.entity(ModEntityTypes.dwarfShepherd), ItemPredicate.ANY),
                        new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, this.entity(ModEntityTypes.dwarfArtificer), ItemPredicate.ANY),
                        new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, this.entity(ModEntityTypes.dwarfToolsmith), ItemPredicate.ANY),
                        new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, this.entity(ModEntityTypes.dwarfMiner), ItemPredicate.ANY),
                        new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, this.entity(ModEntityTypes.dwarfDragonHunter), ItemPredicate.ANY)
                );

        this.advancement("honeycomb")
                .display(ModItems.honeycomb)
                .task(this.items(ModItems.honeycomb));

        this.advancement("magical_honey_cookie").parent("honeycomb")
                .display(ModItems.magicalHoneyCookie)
                .task(this.items(ModItems.magicalHoneyCookie));

    }
}
