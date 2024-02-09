package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient {
    @Unique
    private boolean zombiesExplorer$AAr10 = false;

    @Inject(method = "playSound", at = @At(value = "RETURN"))
    private void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay, CallbackInfo callbackInfo){
        if (soundName.equals("mob.wither.spawn") || soundName.equals("mob.enderdragon.end") || (soundName.equals("mob.guardian.curse") && !zombiesExplorer$AAr10)) {
            zombiesExplorer$AAr10 = soundName.equals("mob.guardian.curse");
            ZombiesExplorer.getInstance().getPowerUpDetect().setGameStart(true);
            ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.clear();
            ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupEnsuredMobList.clear();
            ZombiesExplorer.getInstance().getSpawnPatternNotice().powerupPredictMobList.clear();
            ZombiesExplorer.getInstance().getSpawnPatternNotice().allEntities.clear();
            ZombiesExplorer.getInstance().getSpawnPatternNotice().startCollecting = true;

        }
    }
}
