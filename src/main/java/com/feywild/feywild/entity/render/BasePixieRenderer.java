package com.feywild.feywild.entity.render;

import com.feywild.feywild.entity.base.FlyingFeyBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BasePixieRenderer<T extends FlyingFeyBase> extends GeoEntityRenderer<T> {

    protected BasePixieRenderer(EntityRendererManager manager, AnimatedGeoModel<T> model) {
        super(manager, model);
        this.shadowRadius = 0.2F;
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }
    
    public static <T extends FlyingFeyBase> IRenderFactory<T> create(Supplier<AnimatedGeoModel<T>> modelProvider) {
        return manager -> new BasePixieRenderer<>(manager, modelProvider.get());
    }
}

