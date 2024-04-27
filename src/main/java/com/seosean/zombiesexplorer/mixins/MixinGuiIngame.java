package com.seosean.zombiesexplorer.mixins;

import com.seosean.showspawntime.utils.LanguageUtils;
import com.seosean.showspawntime.utils.PlayerUtils;
import com.seosean.zombiesexplorer.PowerUpDetect;
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
        if (p_175178_1_ == null) {
            return;
        }

        if (!PlayerUtils.isInZombiesTitle()) {
            return;
        }

        new DelayedTask() {
            @Override
            public void run() {
                if (LanguageUtils.isRoundTitle(p_175178_1_)) {
                    PowerUpDetect.detectNextPowerupRound();
                }
            }
        }.runTaskLater(1);
    }
}
