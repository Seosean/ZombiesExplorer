package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import com.seosean.zombiesexplorer.utils.DebugUtils;
import com.seosean.zombiesexplorer.utils.DelayedTask;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {

    @Inject(method = "displayTitle", at = @At(value = "RETURN"))
    private void displayTitle(String p_175178_1_, String p_175178_2_, int p_175178_3_, int p_175178_4_, int p_175178_5_, CallbackInfo callbackInfo){
        ZombiesExplorer.getInstance().getPowerUpDetect().detectNextPowerupRound(p_175178_1_);
    }
}
