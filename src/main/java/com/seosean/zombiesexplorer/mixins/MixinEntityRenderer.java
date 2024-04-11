package com.seosean.zombiesexplorer.mixins;

import com.google.common.base.Predicates;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = {EntityRenderer.class})
public class MixinEntityRenderer {

    @Shadow
    private Minecraft mc;

    @Inject(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getMouseOver (float partialTicks, CallbackInfo ci, Entity entity, double d0, double d1, Vec3 vec3, boolean flag, int i, Vec3 vec31, Vec3 vec32, Vec3 vec33, float f) {
        if (ZombiesExplorer.ENABLED) {
            if (entity instanceof EntityPlayer) {
                List<Entity> entitiesOnLine = new ArrayList<>();
                if (ZombiesExplorer.BadHeadShotOnLine) {
                    List<Entity> entitiesInRange = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * 70, vec31.yCoord * 70, vec31.zCoord * 70).expand((double) f, (double) f, (double) f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
                    Vec3 detectVec = vec3.addVector(vec31.xCoord * 70, vec31.yCoord * 70, vec31.zCoord * 70);
                    for (Entity entity1 : entitiesInRange) {
                        float f1 = 0.4F;
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
        }
    }
}
