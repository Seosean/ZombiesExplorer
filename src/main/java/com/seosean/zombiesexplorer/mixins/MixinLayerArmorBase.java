package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.mixinsinterface.IMixinLayerArmorBase;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LayerArmorBase.class)
public abstract class MixinLayerArmorBase<T extends ModelBase> implements IMixinLayerArmorBase {
    @Shadow
    private float alpha = 1.0F;
    @Shadow
    private float colorR = 1.0F;
    @Shadow
    private float colorG = 1.0F;
    @Shadow
    private float colorB = 1.0F;
    @Shadow
    private boolean field_177193_i;
    @Final
    @Shadow
    private RendererLivingEntity<?> renderer;
    @Shadow
    public abstract ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, int slot, String type);
    @Shadow
    public abstract T func_177175_a(int p_177175_1_);
    @Shadow
    protected abstract T getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, int slot, T model);
    @Shadow
    protected abstract void func_177179_a(T p_177179_1_, int p_177179_2_);
    @Shadow
    protected abstract boolean isSlotForLeggings(int armorSlot);
    @Shadow
    protected abstract void func_177183_a(EntityLivingBase entitylivingbaseIn, T modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_);
    @Shadow
    public abstract ItemStack getCurrentArmor(EntityLivingBase entitylivingbaseIn, int armorSlot);
    @Override
    public void zombiesExplorer$doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale, ZombiesExplorer.RenderType renderType) {
        this.zombiesExplorer$renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 4, renderType);
        this.zombiesExplorer$renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 3, renderType);
        this.zombiesExplorer$renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 2, renderType);
        this.zombiesExplorer$renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 1, renderType);
    }

    @Unique
    private void zombiesExplorer$renderLayer(EntityLivingBase entitylivingbaseIn, float p_177182_2_, float p_177182_3_, float p_177182_4_, float p_177182_5_, float p_177182_6_, float p_177182_7_, float p_177182_8_, int armorSlot, ZombiesExplorer.RenderType renderType)
    {
        ItemStack itemstack = this.getCurrentArmor(entitylivingbaseIn, armorSlot);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor)
        {
            ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
            T t = this.func_177175_a(armorSlot);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_4_);
            t = getArmorModelHook(entitylivingbaseIn, itemstack, armorSlot, t);
            this.func_177179_a(t, armorSlot);
            boolean flag = this.isSlotForLeggings(armorSlot);
            this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemstack, flag ? 2 : 1, null));

            int i = itemarmor.getColor(itemstack);
            {
                if (i != -1) // Allow this for anything, not only cloth.
                {

                    int mixingColor = 0;
                    if(renderType.equals(ZombiesExplorer.RenderType.POWERUP_PREDICT)) {
                        GlStateManager.disableLighting();
                        mixingColor = 0xFF8080;
                    } else if(renderType.equals(ZombiesExplorer.RenderType.POWERUP_ENSURED)) {
                        GlStateManager.disableLighting();
                        mixingColor = 0x800000;
                    } else if (renderType.equals(ZombiesExplorer.RenderType.BAD_HEADSHOT)) {
                        GlStateManager.disableLighting();
                        mixingColor = 0x00FF00;
                    } else if (renderType.equals(ZombiesExplorer.RenderType.DERIVED_BAD_HEADSHOT)) {
                        GlStateManager.disableLighting();
                        mixingColor = 0xFFFF00;
                    } else {
                        GlStateManager.enableLighting();
                    }
                    int color = this.blendColors(i, mixingColor);
                    float f = (float)(color >> 16 & 255) / 255.0F;
                    float f1 = (float)(color >> 8 & 255) / 255.0F;
                    float f2 = (float)(color & 255) / 255.0F;

                    GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
                    t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
                    this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemstack, flag ? 2 : 1, "overlay"));
                }
                { // Non-colored
                    if(renderType.equals(ZombiesExplorer.RenderType.POWERUP_PREDICT)) {
                        GlStateManager.disableLighting();
                        GlStateManager.color(1F, 0.5F, 0.5F, 1.0F);
                    } else if(renderType.equals(ZombiesExplorer.RenderType.POWERUP_ENSURED)) {
                        GlStateManager.disableLighting();
                        GlStateManager.color(0.5F, 0F, 0F, 1.0F);
                    } else if (renderType.equals(ZombiesExplorer.RenderType.BAD_HEADSHOT)) {
                        GlStateManager.disableLighting();
                        GlStateManager.color(0F, 1F, 0F, 1.0F);
                    } else if (renderType.equals(ZombiesExplorer.RenderType.DERIVED_BAD_HEADSHOT)) {
                        GlStateManager.disableLighting();
                        GlStateManager.color(1F, 1F, 0F, 1.0F);
                    } else {
                        GlStateManager.enableLighting();
                        GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
                    }

                    t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
                }
                // Default
                if (!this.field_177193_i && itemstack.hasEffect())
                {
                    this.func_177183_a(entitylivingbaseIn, t, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
                }
            }
        }
    }

    public int blendColors(int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r3 = (int) (0.5 * r1 + 0.5 * r2);
        int g3 = (int) (0.5 * g1 + 0.5 * g2);
        int b3 = (int) (0.5 * b1 + 0.5 * b2);

        return (r3 << 16) | (g3 << 8) | b3;
    }
}
