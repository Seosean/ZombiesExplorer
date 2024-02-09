package com.seosean.zombiesexplorer.mixins;

import com.mojang.authlib.GameProfile;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import com.seosean.zombiesexplorer.mixinsinterface.IMixinLayerCustomHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(LayerCustomHead.class)
public class MixinLayerCustomHead implements IMixinLayerCustomHead {

    @Final
    @Shadow
    private ModelRenderer field_177209_a;

    public void zombiesExplorer$doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale, ZombiesExplorer.RenderType renderType)
    {
        ItemStack itemstack = entitylivingbaseIn.getCurrentArmor(3);

        if (itemstack != null && itemstack.getItem() != null)
        {
            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();

            if (entitylivingbaseIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            boolean flag = entitylivingbaseIn instanceof EntityVillager || entitylivingbaseIn instanceof EntityZombie && ((EntityZombie)entitylivingbaseIn).isVillager();

            if (!flag && entitylivingbaseIn.isChild())
            {
                float f = 2.0F;
                float f1 = 1.4F;
                GlStateManager.scale(f1 / f, f1 / f, f1 / f);
                GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }

            this.field_177209_a.postRender(0.0625F);

            if(renderType.equals(ZombiesExplorer.RenderType.POWERUP_PREDICT)) {
                GlStateManager.disableLighting();
                GlStateManager.color(1F, 0.5F, 0.5F, 1.0F);
            } else if(renderType.equals(ZombiesExplorer.RenderType.POWERUP_ENSURED)) {
                GlStateManager.disableLighting();
                GlStateManager.color(0.5F, 0F, 0F, 1.0F);
            } else if (renderType.equals(ZombiesExplorer.RenderType.BAD_HEADSHOT)) {
                GlStateManager.disableLighting();
                GlStateManager.color(0F, 1F, 0F, 1.0F);
            } else if (renderType.equals(ZombiesExplorer.RenderType.DERIVED_BAD_HEADSHOT)) {
                GlStateManager.disableLighting();
                GlStateManager.color(1F, 1F, 0F, 1.0F);
            } else {
                GlStateManager.enableLighting();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (item instanceof ItemBlock)
            {
                float f2 = 0.625F;
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(f2, -f2, -f2);

                if (flag)
                {
                    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
                }

                minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD);
            }
            else if (item == Items.skull)
            {
                float f3 = 1.1875F;
                GlStateManager.scale(f3, -f3, -f3);

                if (flag)
                {
                    GlStateManager.translate(0.0F, 0.0625F, 0.0F);
                }

                GameProfile gameprofile = null;

                if (itemstack.hasTagCompound())
                {
                    NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                    if (nbttagcompound.hasKey("SkullOwner", 10))
                    {
                        gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                    }
                    else if (nbttagcompound.hasKey("SkullOwner", 8))
                    {
                        String s = nbttagcompound.getString("SkullOwner");

                        if (!StringUtils.isNullOrEmpty(s))
                        {
                            gameprofile = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, s));
                            nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                        }
                    }
                }

                TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemstack.getMetadata(), gameprofile, -1);
            }

            GlStateManager.popMatrix();
        }
    }
}
