package com.seosean.zombiesexplorer.mixinsinterface;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.entity.Entity;

public interface IMixinRenderManager {
    boolean zombiesExplorer$renderEntityStaticWithMixin(Entity entity, float partialTicks, boolean p_178631_3_, ZombiesExplorer.RenderType renderType);
}
