package com.seosean.zombiesexplorer.mixinsinterface;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.entity.Entity;

public interface IMixinModelBase {
    void zombiesExplorer$render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale, ZombiesExplorer.RenderType renderType);
}
