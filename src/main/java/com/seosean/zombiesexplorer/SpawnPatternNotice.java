package com.seosean.zombiesexplorer;

import com.seosean.showspawntime.ShowSpawnTime;
import com.seosean.showspawntime.utils.DebugUtils;
import com.seosean.showspawntime.utils.LanguageUtils;
import com.seosean.zombiesexplorer.utils.DelayedTask;
import com.seosean.zombiesexplorer.utils.Order;
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
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            powerupEnsuredMobList = new ArrayList<>();
            powerupPredictMobList = new ArrayList<>();
            badhsMobList = new ArrayList<>();
            allEntities = new ArrayList<>();
            startCollecting = true;
            return;
        }

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
                entityLivingBase instanceof EntityOcelot ||
                (entityLivingBase instanceof EntityGuardian && entityLivingBase.getMaxHealth() > 30) ||
                (entityLivingBase instanceof EntityChicken && !entityLivingBase.isInvisible())) {


            if (ZombiesExplorer.PowerupDetector) {
                if (!(entityLivingBase instanceof EntitySquid) && !(entityLivingBase instanceof EntityChicken) && !(entityLivingBase instanceof EntityMooshroom) && !(entityLivingBase instanceof EntityWolf && LanguageUtils.getMap().equals(LanguageUtils.ZombiesMap.ALIEN_ARCADIUM) && entityLivingBase.getDistanceSq(-16.5, 72, -0.5) <= 36)) {
                    boolean flag = ShowSpawnTime.getPowerupDetect().insRounds.isEmpty() && ShowSpawnTime.getPowerupDetect().maxRounds.isEmpty() && ShowSpawnTime.getPowerupDetect().ssRounds.isEmpty();
                    int predict = flag ? (ShowSpawnTime.getSpawnTimes().currentRound == 1 ? 1 : 2) : ZombiesExplorer.PowerupPredictor;

                    int amount = PowerUpDetect.amountOfIncomingPowerups;

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
        if (event.phase!= TickEvent.Phase.START) return;
        EntityPlayerSP p = mc.thePlayer;
        if (p == null) return;

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

    @SubscribeEvent
    public void reinitialize(EntityJoinWorldEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null ) return;
        EntityPlayerSP p = mc.thePlayer;
        if(p == null) return;
        if (!event.entity.equals(p)) return;

        PowerUpDetect.amountOfIncomingPowerups = 0;
    }

    public List<String> list = new ArrayList<>();
    public DelayedTask delayedTask;
    @SubscribeEvent
    public void onDetectSpawnOrder(EntityJoinWorldEvent event) {
        if (!ZombiesExplorer.MobSpawnOrder) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null ) return;
        EntityPlayerSP p = mc.thePlayer;
        if(p == null) return;

        if (!LanguageUtils.getMap().equals(LanguageUtils.ZombiesMap.ALIEN_ARCADIUM)) {
            return;
        }

        if (!(event.entity instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase entityLivingBase = (EntityLivingBase) event.entity;
        if (entityLivingBase instanceof EntityIronGolem ||
                entityLivingBase instanceof EntityGhast ||
                entityLivingBase instanceof EntityBlaze ||
                entityLivingBase instanceof EntitySlime ||
                entityLivingBase instanceof EntityMagmaCube ||
                entityLivingBase instanceof EntityGiantZombie) {
            String name = entityLivingBase.getName();

            if (entityLivingBase instanceof EntityIronGolem) {
                name = EnumChatFormatting.WHITE + name;
            } else if (entityLivingBase instanceof EntityGhast) {
                name = EnumChatFormatting.LIGHT_PURPLE + name;
            } else if (entityLivingBase instanceof EntityBlaze) {
                name = EnumChatFormatting.GOLD + name;
            } else if (entityLivingBase instanceof EntityMagmaCube) {
                name = EnumChatFormatting.RED + name;
            } else if (entityLivingBase instanceof EntitySlime) {
                name = EnumChatFormatting.GREEN + name;
            } else if (entityLivingBase instanceof EntityGiantZombie) {
                name = EnumChatFormatting.AQUA + name;
            }


            list.add(name);

            if (delayedTask != null) {
                delayedTask.cancel();
            }

            delayedTask = new DelayedTask() {
                @Override
                public void run() {
                    List<String> newOrderList = list.stream().distinct().collect(Collectors.toList());
                    Order.input(newOrderList);

                    list = new ArrayList<>();
                    delayedTask = null;
                }
            }.runTaskLater(19);
        }
    }
}
