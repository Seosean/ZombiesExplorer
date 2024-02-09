package com.seosean.zombiesexplorer.mixins;

import com.google.common.base.Predicates;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(value = {EntityRenderer.class})
public class MixinEntityRenderer {

    @Shadow
    private Minecraft mc;

    @Shadow
    private Entity pointedEntity;

    @Inject(method = "getMouseOver", at = @At("HEAD"), cancellable = true)
    private void getMouseOver(float partialTicks, CallbackInfo ci) {
        Entity entity = this.mc.getRenderViewEntity();

        if (entity != null)
        {
            if (this.mc.theWorld != null)
            {
                this.mc.mcProfiler.startSection("pick");
                this.mc.pointedEntity = null;
                double d0 = this.mc.playerController.getBlockReachDistance();
                this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
                double d1 = d0;
                Vec3 vec3 = entity.getPositionEyes(partialTicks);
                boolean flag = false;

                if (this.mc.playerController.extendedReach())
                {
                    d0 = 6.0D;
                    d1 = 6.0D;
                }
                else if (d0 > 3.0D) {
                    flag = true;
                }

                if (this.mc.objectMouseOver != null)
                {
                    d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3 vec31 = entity.getLook(partialTicks);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                this.pointedEntity = null;
                Vec3 vec33 = null;
                float f = 1.0F;
                List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));

                double d2 = d1;

                List<Entity> entitiesOnLine = new ArrayList<>();

                for (Entity entity1 : list) {
                    float f1 = entity1.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f1, (double) f1, (double) f1);
                    MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                    if (axisalignedbb.isVecInside(vec3)) {
                        if (d2 >= 0.0D) {
                            this.pointedEntity = entity1;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                            d2 = 0.0D;
                        }
                    } else if (movingobjectposition != null) {
                        double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                        if (d3 < d2 || d2 == 0.0D) {
                            if ((Entity) entity1 == entity.ridingEntity && !entity.canRiderInteract()) {
                                if (d2 == 0.0D) {
                                    this.pointedEntity = entity1;
                                    vec33 = movingobjectposition.hitVec;
                                }
                            } else {
                                this.pointedEntity = (Entity) entity1;
                                vec33 = movingobjectposition.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
                if (entity instanceof EntityPlayer) {
                    if (ZombiesExplorer.BadHeadShotOnLine) {
                        List<Entity> entitiesInRange = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * 70, vec31.yCoord * 70, vec31.zCoord * 70).expand((double) f, (double) f, (double) f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
                        Vec3 detectVec = vec3.addVector(vec31.xCoord * 70, vec31.yCoord * 70, vec31.zCoord * 70);
                        for (Entity entity1 : entitiesInRange) {
                            float f1 = 0.3F;
                            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, 0.1, f1);
                            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, detectVec);

                            if (movingobjectposition != null) {
                                if (((EntityPlayer) entity).canEntityBeSeen(entity1)) {
                                    entitiesOnLine.add(entity1);
                                }
                            }
                        }

                        int size = ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.size();
                        if (ZombiesExplorer.BadHeadShotDetector) {
                            if (size != 0 && entitiesOnLine.contains(ZombiesExplorer.getInstance().getSpawnPatternNotice().badhsMobList.get(size - 1))) {
                                ZombiesExplorer.getInstance().getSpawnPatternNotice().entitiesOnLine = entitiesOnLine.stream().filter(e -> e instanceof EntityLivingBase).filter(e -> ZombiesExplorer.getInstance().getSpawnPatternNotice().allEntities.contains(e)).collect(Collectors.toList());
                            } else {
                                ZombiesExplorer.getInstance().getSpawnPatternNotice().entitiesOnLine.clear();
                            }
                        } else {
                            ZombiesExplorer.getInstance().getSpawnPatternNotice().entitiesOnLine.clear();
                        }
                    } else {
                        ZombiesExplorer.getInstance().getSpawnPatternNotice().entitiesOnLine.clear();
                    }
                }

                if (this.pointedEntity != null && flag) {
                    double maxDistance = 3.0D;
                    if (vec3.distanceTo(vec33) > maxDistance) {
                        this.pointedEntity = null;
                        this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, Objects.requireNonNull(vec33), null, new BlockPos(vec33));
                    }
                }

                if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null))
                {
                    this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);

                    if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame)
                    {
                        this.mc.pointedEntity = this.pointedEntity;
                    }
                }

                this.mc.mcProfiler.endSection();
            }
        }

        ci.cancel();
    }



}
