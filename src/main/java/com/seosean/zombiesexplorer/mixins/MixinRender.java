package com.seosean.zombiesexplorer.mixins;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {

    @Shadow
    protected abstract boolean bindEntityTexture(T entity);

    @Final
    @Shadow
    protected RenderManager renderManager;;

    @Shadow
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.renderName(entity, x, y, z);
    }

    @Shadow
    public abstract FontRenderer getFontRendererFromRenderManager();

    @Shadow
    protected abstract void renderName(T entity, double x, double y, double z);

}
