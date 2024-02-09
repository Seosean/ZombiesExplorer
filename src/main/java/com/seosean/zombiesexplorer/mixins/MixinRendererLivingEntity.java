package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.mixinsinterface.IMixinLayerArmorBase;
import com.seosean.zombiesexplorer.mixinsinterface.IMixinLayerCustomHead;
import com.seosean.zombiesexplorer.mixinsinterface.IMixinModelBase;
import com.seosean.zombiesexplorer.mixinsinterface.IMixinRendererLivingEntity;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.minecraft.client.renderer.entity.RendererLivingEntity.NAME_TAG_RANGE;
import static net.minecraft.client.renderer.entity.RendererLivingEntity.NAME_TAG_RANGE_SNEAK;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends MixinRender<T> implements IMixinRendererLivingEntity {

    @Unique
    private static final Logger zombiesExplorer$logger = LogManager.getLogger();

    @Shadow
    protected abstract float getSwingProgress(T livingBase, float partialTickTime);

    @Shadow
    protected abstract float interpolateRotation(float par1, float par2, float par3);

    @Shadow
    protected abstract void renderLivingAt(T entityLivingBaseIn, double x, double y, double z);

    @Shadow
    protected abstract float handleRotationFloat(T livingBase, float partialTicks);

    @Shadow
    protected abstract void rotateCorpse(T bat, float p_77043_2_, float p_77043_3_, float partialTicks);

    @Shadow
    protected boolean renderOutlines;

    @Shadow
    protected abstract boolean setScoreTeamColor(T entityLivingBaseIn);

    @Shadow
    protected abstract void preRenderCallback(T entitylivingbaseIn, float partialTickTime);

    @Shadow
    protected abstract void unsetScoreTeamColor();

    @Shadow
    protected abstract void unsetBrightness();

    @Shadow
    protected abstract boolean canRenderName(T entity);

    @Shadow
    protected List<LayerRenderer<T>> layerRenderers;

    @Shadow
    protected abstract boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures);

    @Shadow
    protected abstract boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks);

    @Shadow
    protected abstract void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_);

    @Inject(method = "doRender*", at = @At(value = "HEAD"), cancellable = true)
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (zombiesExplorer$renderType != null) {
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre(entity, (RendererLivingEntity) (Object) this, x, y, z)))
                return;

            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            this.mainModel.swingProgress = this.getSwingProgress((T) entity, partialTicks);
            boolean shouldSit = entity.isRiding() && (entity.ridingEntity != null && entity.ridingEntity.shouldRiderSit());
            this.mainModel.isRiding = shouldSit;
            this.mainModel.isChild = entity.isChild();

            try {
                float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
                float f2 = f1 - f;

                if (shouldSit && entity.ridingEntity instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) entity.ridingEntity;
                    f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                    f2 = f1 - f;
                    float f3 = MathHelper.wrapAngleTo180_float(f2);

                    if (f3 < -85.0F) {
                        f3 = -85.0F;
                    }

                    if (f3 >= 85.0F) {
                        f3 = 85.0F;
                    }

                    f = f1 - f3;

                    if (f3 * f3 > 2500.0F) {
                        f += f3 * 0.2F;
                    }
                }

                float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                this.renderLivingAt((T) entity, x, y, z);
                float f8 = this.handleRotationFloat((T) entity, partialTicks);
                this.rotateCorpse((T) entity, f8, f, partialTicks);
                GlStateManager.enableRescaleNormal();
                GlStateManager.scale(-1.0F, -1.0F, 1.0F);
                this.preRenderCallback((T) entity, partialTicks);
                float f4 = 0.0625F;
                GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
                float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

                if (entity.isChild()) {
                    f6 *= 3.0F;
                }

                if (f5 > 1.0F) {
                    f5 = 1.0F;
                }

                GlStateManager.enableAlpha();
                this.mainModel.setLivingAnimations((T) entity, f6, f5, partialTicks);
                this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, 0.0625F, (T) entity);

                if (this.renderOutlines) {
                    boolean flag1 = this.setScoreTeamColor((T) entity);
                    this.zombiesExplorer$renderModel((T) entity, f6, f5, f8, f2, f7, 0.0625F, zombiesExplorer$renderType);
                    if (flag1) {
                        this.unsetScoreTeamColor();
                    }

                } else {
                    boolean flag = this.setDoRenderBrightness((T) entity, partialTicks);
                    this.zombiesExplorer$renderModel((T) entity, f6, f5, f8, f2, f7, 0.0625F, zombiesExplorer$renderType);

                    if (flag) {
                        this.unsetBrightness();
                    }

                    GlStateManager.depthMask(true);

                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
                        this.zombiesExplorer$renderLayers((T) entity, f6, f5, partialTicks, f8, f2, f7, 0.0625F, zombiesExplorer$renderType);
                    }
                }

                GlStateManager.disableRescaleNormal();
            } catch (Exception exception) {
                zombiesExplorer$logger.error((String) "Couldn\'t render entity", (Throwable) exception);
            }

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
            if (!this.renderOutlines) {
                if (ZombiesExplorer.NameTag) {
                    if (zombiesExplorer$renderType != null) {
                        this.zombiesExplorer$renderNameWithMixin((T) entity, x, y, z, zombiesExplorer$renderType);
                    } else {
                        super.doRender((T) entity, x, y, z, entityYaw, partialTicks);
                    }
                } else {
                    super.doRender((T) entity, x, y, z, entityYaw, partialTicks);
                }
            }
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post(entity, (RendererLivingEntity) (Object) this, x, y, z));
            ci.cancel();
        }
    }

    private ZombiesExplorer.RenderType zombiesExplorer$renderType;
    @Shadow
    public abstract void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks);

    @Override
    public void zombiesExplorer$DoRenderWithMixin(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, ZombiesExplorer.RenderType renderType)
    {
        this.zombiesExplorer$renderType = renderType;
        this.doRender((T) entity, x, y, z, entityYaw, partialTicks);
        this.zombiesExplorer$renderType = null;
    }

    @Shadow
    protected ModelBase mainModel;

    @Unique
    protected void zombiesExplorer$renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_, ZombiesExplorer.RenderType renderType)
    {
        if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
        }

        if (mainModel instanceof IMixinModelBase) {
            ((IMixinModelBase) mainModel).zombiesExplorer$render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_, renderType);
        } else {
            mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        }
    }

    @Unique
    public void zombiesExplorer$renderNameWithMixin(T entity, double x, double y, double z, ZombiesExplorer.RenderType renderType)
    {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre(entity, (RendererLivingEntity) (Object) this, x, y, z))) return;
        double d0 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
        float f = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;

        if (d0 < (double)(f * f))
        {
            String s = "";
            if(renderType.equals(ZombiesExplorer.RenderType.POWERUP_PREDICT)) {
                s = EnumChatFormatting.RED.toString() + EnumChatFormatting.STRIKETHROUGH + "Powerup";
            } else if(renderType.equals(ZombiesExplorer.RenderType.POWERUP_ENSURED)) {
                s = EnumChatFormatting.DARK_RED + "Powerup";
            } else if (renderType.equals(ZombiesExplorer.RenderType.BAD_HEADSHOT)) {
                s = EnumChatFormatting.GREEN + "Bad Headshot";
            } else if (renderType.equals(ZombiesExplorer.RenderType.DERIVED_BAD_HEADSHOT)) {
                s = EnumChatFormatting.YELLOW + "Bad Headshot";
            }

            float f1 = 0.02666667F;
            GlStateManager.alphaFunc(516, 0.1F);

            if (entity.isSneaking())
            {
                FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)x, (float)y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float)z);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.disableTexture2D();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int i = fontrenderer.getStringWidth(s) / 2;
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos((double)(-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                worldrenderer.pos((double)(-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                worldrenderer.pos((double)(i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                worldrenderer.pos((double)(i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                tessellator.draw();
                GlStateManager.enableTexture2D();
                GlStateManager.depthMask(true);
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }
            else
            {
                this.zombiesExplorer$renderLivingLabel(entity, s, x, y - (entity.isChild() ? (double)(entity.height / 2.0F) : 0.0D), z, 64);
            }
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Specials.Post(entity, (RendererLivingEntity) (Object) this, x, y, z));
    }

    @Unique
    protected void zombiesExplorer$renderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance)
    {
        double d0 = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);

        if (d0 <= (double)(maxDistance * maxDistance))
        {
            FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x + 0.0F, (float)y + entityIn.height + 0.5F, (float)z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
//            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int i = 0;

            if (str.equals("deadmau5"))
            {
                i = -10;
            }

            int j = fontrenderer.getStringWidth(str) / 2;
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos((double)(-j - 1), (double)(-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double)(-j - 1), (double)(8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double)(j + 1), (double)(8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double)(j + 1), (double)(-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    @Unique
    protected void zombiesExplorer$renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_, ZombiesExplorer.RenderType renderType) {
        for (LayerRenderer<T> layerrenderer : this.layerRenderers)
        {
            boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
            if (layerrenderer instanceof IMixinLayerArmorBase) {
                ((IMixinLayerArmorBase) layerrenderer).zombiesExplorer$doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_, renderType);
            } else if (layerrenderer instanceof IMixinLayerCustomHead){
                ((IMixinLayerCustomHead) layerrenderer).zombiesExplorer$doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_, renderType);
            } else {
                layerrenderer.doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
            }
            if (flag)
            {
                this.unsetBrightness();
            }
        }
    }

}
