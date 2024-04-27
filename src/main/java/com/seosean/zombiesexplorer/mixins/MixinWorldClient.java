package com.seosean.zombiesexplorer.mixins;

import com.seosean.showspawntime.ShowSpawnTime;
import com.seosean.showspawntime.handler.GameTickHandler;
import com.seosean.showspawntime.utils.LanguageUtils;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import com.seosean.zombiesexplorer.utils.Order;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(WorldClient.class)
public class MixinWorldClient {
    @Unique
    private boolean zombiesExplorer$AAr10 = false;

    @Inject(method = "playSound", at = @At(value = "RETURN"))
    private void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay, CallbackInfo callbackInfo){
        if (soundName.equals("mob.wither.spawn") || soundName.equals("mob.enderdragon.end") || (soundName.equals("mob.guardian.curse") && LanguageUtils.getMap().equals(LanguageUtils.ZombiesMap.ALIEN_ARCADIUM) && !zombiesExplorer$AAr10)) {
            zombiesExplorer$AAr10 = soundName.equals("mob.guardian.curse");
            ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList = new ArrayList<>();
            ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupEnsuredMobList = new ArrayList<>();
            ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupPredictMobList = new ArrayList<>();
            ZombiesExplorer.getInstance().getSpawnPatternNotice().allEntities = new ArrayList<>();
            ZombiesExplorer.getInstance().getSpawnPatternNotice().startCollecting = true;

            if (ShowSpawnTime.getSpawnTimes().currentRound == 1) {
                ZombiesExplorer.getInstance().getSpawnPatternNotice().list = new ArrayList<>();
                Order.orderList = new ArrayList<>();
            }
        }
    }
}
