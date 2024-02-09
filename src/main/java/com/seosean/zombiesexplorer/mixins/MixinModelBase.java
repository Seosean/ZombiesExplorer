package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.mixinsinterface.IMixinModelBase;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelBase.class)
public abstract class MixinModelBase implements IMixinModelBase {

    @Shadow
    public boolean isChild;

    @Shadow
    public abstract void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale);

    @Override
    public void zombiesExplorer$render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale, ZombiesExplorer.RenderType renderType) {
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
            GlStateManager.color(1F, 1F, 1F, 1.0F);
        }
        this.render(entityIn, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale);
    }

//            if(ZombiesExplorer.PowerupDetector && ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupMobList.contains(entityIn) && ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupMobList.indexOf(entityIn) >= ZombiesExplorer.getInstance().getPowerUpDetect().amountOfIncomingPowerups) {
//        GlStateManager.disableLighting();
//        GlStateManager.color(1F, 0.5F, 0.5F, 1.0F);
//    } else if(ZombiesExplorer.PowerupDetector && ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupMobList.contains(entityIn)) {
//        GlStateManager.disableLighting();
//        GlStateManager.color(0.5F, 0F, 0F, 1.0F);
//    } else if (ZombiesExplorer.BadHeadShotDetector && ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.contains(entityIn) && ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.get(ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.size() - 1).equals(entityIn)) {
//        GlStateManager.disableLighting();
//        GlStateManager.color(0F, 1F, 0F, 1.0F);
//    } else if (ZombiesExplorer.BadHeadShotDetector && ZombiesExplorer.getInstance().getSpawnPatternNotice().entitiesOnLine.contains(entityIn)) {
//        GlStateManager.disableLighting();
//        GlStateManager.color(1F, 1F, 0F, 1.0F);
//    } else {
//        GlStateManager.enableLighting();
//    }

}
