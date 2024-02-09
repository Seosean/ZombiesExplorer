package com.seosean.zombiesexplorer.mixinsinterface;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.entity.EntityLivingBase;

public interface IMixinRendererLivingEntity {
    <T extends EntityLivingBase>  void zombiesExplorer$DoRenderWithMixin(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, ZombiesExplorer.RenderType renderType);
}
