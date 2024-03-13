package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import com.seosean.zombiesexplorer.mixinsinterface.IMixinRenderManager;
import com.seosean.zombiesexplorer.mixinsinterface.IMixinRendererLivingEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ReportedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager implements IMixinRenderManager {
    @Shadow
    private double renderPosX;
    @Shadow
    private double renderPosY;
    @Shadow
    private double renderPosZ;
    @Shadow
    public TextureManager renderEngine;
    @Shadow
    private boolean debugBoundingBox;
    @Shadow
    private boolean renderOutlines;
    @Shadow
    public abstract <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn);
    @Shadow
    protected abstract void renderDebugBoundingBox(Entity entityIn, double p_85094_2_, double p_85094_4_, double p_85094_6_, float p_85094_8_, float p_85094_9_);
    @Override
    public boolean zombiesExplorer$renderEntityStaticWithMixin(Entity entity, float partialTicks, boolean p_178631_3_, ZombiesExplorer.RenderType renderType) {
        return this.zombiesExplorer$renderEntityStatic(entity, partialTicks, p_178631_3_, renderType);
    }

    @Unique
    public boolean zombiesExplorer$renderEntityStatic(Entity entity, float partialTicks, boolean p_147936_3_, ZombiesExplorer.RenderType renderType)
    {
        if (entity.ticksExisted == 0)
        {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
        }

        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        int i = entity.getBrightnessForRender(partialTicks);

        if (entity.isBurning())
        {
            i = 15728880;
        }

        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return this.zombiesExplorer$doRenderEntity(entity, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ, f, partialTicks, p_147936_3_, renderType);
    }

    @Unique
    public <T extends Entity> boolean zombiesExplorer$doRenderEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean p_147939_10_, ZombiesExplorer.RenderType renderType)
    {
        Render<Entity> render = null;

        try
        {
            render = this.getEntityRenderObject(entity);

            if (render != null && this.renderEngine != null)
            {
                try
                {
                    if (render instanceof RendererLivingEntity)
                    {
                        ((RendererLivingEntity<?>)render).setRenderOutlines(this.renderOutlines);
                    }

                    if (render instanceof IMixinRendererLivingEntity) {
                        ((IMixinRendererLivingEntity) render).zombiesExplorer$DoRenderWithMixin((EntityLivingBase) entity, x, y, z, entityYaw, partialTicks, renderType);
                    } else {
                        render.doRender(entity, x, y, z, entityYaw, partialTicks);
                    }
                }
                catch (Throwable throwable2)
                {
                    throw new ReportedException(CrashReport.makeCrashReport(throwable2, "Rendering entity in world"));
                }

                try
                {
                    if (!this.renderOutlines)
                    {
                        render.doRenderShadowAndFire(entity, x, y, z, entityYaw, partialTicks);
                    }
                }
                catch (Throwable throwable1)
                {
                    throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Post-rendering entity in world"));
                }

                if (this.debugBoundingBox && !entity.isInvisible() && !p_147939_10_)
                {
                    try
                    {
                        this.renderDebugBoundingBox(entity, x, y, z, entityYaw, partialTicks);
                    }
                    catch (Throwable throwable)
                    {
                        throw new ReportedException(CrashReport.makeCrashReport(throwable, "Rendering entity hitbox in world"));
                    }
                }
            }
            else return this.renderEngine == null;

            return true;
        }
        catch (Throwable throwable3)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
            entity.addEntityCrashInfo(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
            crashreportcategory1.addCrashSection("Assigned renderer", render);
            crashreportcategory1.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
            crashreportcategory1.addCrashSection("Rotation", entityYaw);
            crashreportcategory1.addCrashSection("Delta", partialTicks);
            throw new ReportedException(crashreport);
        }
    }
}
