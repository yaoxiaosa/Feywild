package com.feywild.feywild.item;

import com.feywild.feywild.FeywildMod;
import com.feywild.feywild.entity.AutumnPixieEntity;
import com.feywild.feywild.entity.ModEntityTypes;
import com.feywild.feywild.util.KeyboardHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class SummoningScrollAutumnPixie extends Item {

    public SummoningScrollAutumnPixie() {
        super(new Item.Properties().tab(FeywildMod.FEYWILD_TAB));
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(!context.getLevel().isClientSide){

            //TAMED
            AutumnPixieEntity entity = new AutumnPixieEntity(context.getLevel(), true, context.getClickedPos());
            //summons pixie
            entity.setPos(context.getClickLocation().x(), context.getClickLocation().y() + 1, context.getClickLocation().z());
            context.getLevel().addFreshEntity(entity);

            context.getPlayer().getItemInHand(context.getHand()).shrink(1);
        }
        return ActionResultType.SUCCESS;
    }


    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag){

        if(KeyboardHelper.isHoldingShift()){

            tooltip.add(new TranslationTextComponent("message.feywild.autumn_pixie"));

        }
        else {
            tooltip.add(new TranslationTextComponent("message.feywild.itemmessage"));

        }

        super.appendHoverText(stack, world, tooltip, flag);
    }
}
