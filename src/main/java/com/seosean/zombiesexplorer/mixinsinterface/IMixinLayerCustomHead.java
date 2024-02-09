package com.seosean.zombiesexplorer.mixinsinterface;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.entity.EntityLivingBase;

public interface IMixinLayerCustomHead {
    void zombiesExplorer$doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale, ZombiesExplorer.RenderType renderType);

}
