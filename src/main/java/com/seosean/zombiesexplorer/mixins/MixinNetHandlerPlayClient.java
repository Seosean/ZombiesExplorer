package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.PowerUpDetect;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import com.seosean.zombiesexplorer.utils.DebugUtils;
import com.seosean.zombiesexplorer.utils.GameUtils;
import com.seosean.zombiesexplorer.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Inject(method = "handleEntityMetadata", at = @At(value = "RETURN"))
    public void handleEntityMetadata(S1CPacketEntityMetadata packetIn, CallbackInfo callbackInfo) {
        PowerUpDetect powerUpDetect = ZombiesExplorer.getInstance().getPowerUpDetect();
        int gameTick = powerUpDetect.getGameTick();
        if (Minecraft.getMinecraft().theWorld.getEntityByID(packetIn.getEntityId()) instanceof EntityArmorStand) {
            for (DataWatcher.WatchableObject watchableObject : packetIn.func_149376_c()) {
                if (watchableObject.getObjectType() == 4 && watchableObject.getDataValueId() == 2) {
                    if (watchableObject.getObject() instanceof String) {
                        String armorStandName = StringUtils.trim((String) watchableObject.getObject());
                        int round = ZombiesExplorer.getInstance().getPowerUpDetect().getCurrentRound();

                        if (round == 0) {
                            return;
                        }

                        List<Integer> roundList2 = new ArrayList<>();
                        List<Integer> roundList3 = new ArrayList<>();
                        if (armorStandName.contains("MAX AMMO") || armorStandName.contains("弹药满载") || armorStandName.contains("填滿彈藥")) {
                            roundList2.addAll(Arrays.asList(PowerUpDetect.r2MaxRoundsAA));
                            roundList3.addAll(Arrays.asList(PowerUpDetect.r3MaxRoundsAA));
                            if (roundList2.contains(round)) {
                                powerUpDetect.setMaxRound(2);
                            } else if (roundList3.contains(round) && gameTick <= 1000) {
                                powerUpDetect.setMaxRound(2);
                            } else if (roundList3.contains(round)) {
                                powerUpDetect.setMaxRound(3);
                            } else if (roundList3.contains(round - 1) && gameTick <= 1000) {
                                powerUpDetect.setMaxRound(3);
                            }
                        }
                        else if (armorStandName.contains("INSTA KILL") || armorStandName.contains("瞬间击杀") || armorStandName.contains("一擊必殺")) {
                            roundList2.addAll(Arrays.asList(PowerUpDetect.r2InsRoundsAA));
                            roundList3.addAll(Arrays.asList(PowerUpDetect.r3InsRoundsAA));
                            if (roundList2.contains(round)) {
                                powerUpDetect.setInstaRound(2);
                            } else if (gameTick <= 1000 && roundList3.contains(round)) {
                                powerUpDetect.setInstaRound(2);
                            } else if (roundList3.contains(round)) {
                                powerUpDetect.setInstaRound(3);
                            } else if (roundList3.contains(round - 1) && gameTick <= 1000) {
                                powerUpDetect.setInstaRound(3);
                            }
                        } else if (armorStandName.contains("SHOPPING SPREE") || armorStandName.contains("购物狂潮") || armorStandName.contains("購物狂潮")) {
                            roundList2.addAll(Arrays.asList(PowerUpDetect.r5SSRoundsAA));
                            roundList3.addAll(Arrays.asList(PowerUpDetect.r6SSRoundsAA));
                            List<Integer> roundList4 = new ArrayList<>(Arrays.asList(PowerUpDetect.r7SSRoundsAA));
                            if (roundList2.contains(round)) {
                                powerUpDetect.setSSRound(5);
                            } else if (roundList3.contains(round) && gameTick <= 1000) {
                                powerUpDetect.setSSRound(5);
                            } else if (roundList3.contains(round)) {
                                powerUpDetect.setSSRound(6);
                            } else if (roundList4.contains(round) && gameTick <= 1000) {
                                powerUpDetect.setSSRound(6);
                            } else if (roundList4.contains(round)) {
                                powerUpDetect.setSSRound(7);
                            } else if (roundList4.contains(round - 1) && gameTick <= 1000) {
                                powerUpDetect.setSSRound(7);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }
}
