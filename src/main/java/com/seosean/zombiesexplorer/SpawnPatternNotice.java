package com.seosean.zombiesexplorer;

import com.seosean.zombiesexplorer.utils.DebugUtils;
import com.seosean.zombiesexplorer.utils.GameUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class SpawnPatternNotice {
    public boolean startCollecting = true;
    public List<EntityLivingBase> powerupEnsuredMobList = new ArrayList<>();
    public List<EntityLivingBase> powerupPredictMobList = new ArrayList<>();
    public List<EntityLivingBase> badhsMobList = new ArrayList<>();
    public List<EntityLivingBase> allEntities = new ArrayList<>();

    public List<Entity> entitiesOnLine = new ArrayList<>();

    @SubscribeEvent
    public void onZombiesSpawn(EntityJoinWorldEvent event) {
        Entity entity = event.entity;
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
        if (entity.equals(Minecraft.getMinecraft().thePlayer)) {
            powerupEnsuredMobList.clear();
            powerupPredictMobList.clear();
            badhsMobList.clear();
            allEntities.clear();
            startCollecting = true;
            return;
        }

        PowerUpDetect powerUpDetect = ZombiesExplorer.INSTANCE.getPowerUpDetect();
        if (entityLivingBase instanceof EntityZombie ||
                entityLivingBase instanceof EntitySlime ||
                entityLivingBase instanceof EntityWolf ||
                entityLivingBase instanceof EntityWitch ||
                entityLivingBase instanceof EntityEndermite ||
                entityLivingBase instanceof EntityCreeper ||
                entityLivingBase instanceof EntityBlaze ||
                entityLivingBase instanceof EntitySkeleton ||
                entityLivingBase instanceof EntityGhast ||
                entityLivingBase instanceof EntityGolem ||
                entityLivingBase instanceof EntitySquid ||
                entityLivingBase instanceof EntitySilverfish ||
                entityLivingBase instanceof EntityGiantZombie ||
                entityLivingBase instanceof EntityCaveSpider ||
                entityLivingBase instanceof EntityMooshroom ||
                (entityLivingBase instanceof EntityChicken && !entityLivingBase.isInvisible())) {

            if (entityLivingBase.ticksExisted >= 20) {
                return;
            }

            if (ZombiesExplorer.PowerupDetector) {
                if (!(entityLivingBase instanceof EntityBlaze || entityLivingBase instanceof EntityEndermite || entityLivingBase instanceof EntitySquid || (entityLivingBase instanceof EntitySlime && ((EntitySlime) entityLivingBase).getSlimeSize() == 2)
                || entityLivingBase instanceof EntityChicken || entityLivingBase instanceof EntityMooshroom || (GameUtils.getMap().equals(GameUtils.ZombiesMap.ALIEN_ARCADIUM) && entityLivingBase instanceof EntityWolf && entityLivingBase.getHealth() <= 10))) {
                    boolean flag = powerUpDetect.insRounds.isEmpty() && powerUpDetect.maxRounds.isEmpty() && powerUpDetect.ssRounds.isEmpty();
                    int predict = flag ? (powerUpDetect.getCurrentRound() == 1 ? 1 : 2) : ZombiesExplorer.PowerupPredictor;
                    int amount = powerUpDetect.amountOfIncomingPowerups;

                    if (startCollecting) {
                        if (powerupEnsuredMobList.size() == amount && powerupPredictMobList.size() < predict) {
                            if (!powerupPredictMobList.contains(entityLivingBase) && !powerupEnsuredMobList.contains(entityLivingBase)) {
                                powerupPredictMobList.add(entityLivingBase);
                            }
                        }

                        if (powerupEnsuredMobList.size() < amount) {
                            if (!powerupEnsuredMobList.contains(entityLivingBase) && !powerupPredictMobList.contains(entityLivingBase)) {
                                powerupEnsuredMobList.add(entityLivingBase);
                            }
                        }

                        if (powerupEnsuredMobList.size() == amount && powerupPredictMobList.size() == predict) {
                            startCollecting = false;
                        }
//
//                        DebugUtils.sendMessage("===============================================");
//                        DebugUtils.sendMessage("amountOfIncomingPowerups: " + ZombiesExplorer.getInstance().getPowerUpDetect().amountOfIncomingPowerups);
//                        DebugUtils.sendMessage("amount: " + amount);
//                        DebugUtils.sendMessage("powerupEnsuredMobList: " + powerupEnsuredMobList.size());
//                        DebugUtils.sendMessage("powerupPredictMobList: " + powerupPredictMobList.size());
//                        DebugUtils.sendMessage("===============================================");
                    }
                }
            }

            if (ZombiesExplorer.BadHeadShotDetector) {
                if (!badhsMobList.contains(entityLivingBase)) {
                    badhsMobList.add(entityLivingBase);
                }
            }

            allEntities.add(entityLivingBase);
        }
    }

    @SubscribeEvent
    public void setDisplayTime(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null ) return;
        if(event.phase!= TickEvent.Phase.START) return;
        EntityPlayerSP p = mc.thePlayer;
        if(p == null) return;
        List<EntityLivingBase> list = new ArrayList<>();

        list.addAll(this.allEntities);
        list.addAll(this.badhsMobList);
        for (EntityLivingBase entity : list) {
            if (entity.isDead || entity.getHealth() == 0) {
                allEntities.remove(entity);
                badhsMobList.remove(entity);
            }
        }
    }


}
