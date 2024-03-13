package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import com.seosean.zombiesexplorer.mixinsinterface.IMixinRenderManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {

    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntitySimple(Lnet/minecraft/entity/Entity;F)Z", ordinal = 2))
    private boolean renderEntities(RenderManager instance, Entity entityIn, float partialTicks) {
        ZombiesExplorer.RenderType renderType;
        if (ZombiesExplorer.ENABLED) {
            if (entityIn instanceof EntityLivingBase && !entityIn.isDead && ((EntityLivingBase) entityIn).getHealth() != 0 && ZombiesExplorer.PowerupDetector && ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupPredictMobList.contains(entityIn)) {
                renderType = ZombiesExplorer.RenderType.POWERUP_PREDICT;
                return ((IMixinRenderManager) instance).zombiesExplorer$renderEntityStaticWithMixin(entityIn, partialTicks, false, renderType);
            } else if (entityIn instanceof EntityLivingBase && !entityIn.isDead && ((EntityLivingBase) entityIn).getHealth() != 0 && ZombiesExplorer.PowerupDetector && ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupEnsuredMobList.contains(entityIn)) {
                renderType = ZombiesExplorer.RenderType.POWERUP_ENSURED;
                return ((IMixinRenderManager) instance).zombiesExplorer$renderEntityStaticWithMixin(entityIn, partialTicks, false, renderType);
            } else if (entityIn instanceof EntityLivingBase && !entityIn.isDead && ((EntityLivingBase) entityIn).getHealth() != 0 && ZombiesExplorer.BadHeadShotDetector && ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.contains(entityIn) && ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.get(ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.size() - 1).equals(entityIn)) {
                renderType = ZombiesExplorer.RenderType.BAD_HEADSHOT;
                return ((IMixinRenderManager) instance).zombiesExplorer$renderEntityStaticWithMixin(entityIn, partialTicks, false, renderType);
            } else if (entityIn instanceof EntityLivingBase && !entityIn.isDead && ((EntityLivingBase) entityIn).getHealth() != 0 && ZombiesExplorer.BadHeadShotDetector && ZombiesExplorer.getInstance().getSpawnPatternNotice().entitiesOnLine.contains(entityIn)) {
                renderType = ZombiesExplorer.RenderType.DERIVED_BAD_HEADSHOT;
                return ((IMixinRenderManager) instance).zombiesExplorer$renderEntityStaticWithMixin(entityIn, partialTicks, false, renderType);
            }
        }
        return instance.renderEntitySimple(entityIn, partialTicks);
    }

}
