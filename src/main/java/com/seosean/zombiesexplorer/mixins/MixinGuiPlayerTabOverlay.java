package com.seosean.zombiesexplorer.mixins;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import com.seosean.zombiesexplorer.utils.DebugUtils;
import com.seosean.zombiesexplorer.utils.Order;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

    @Shadow
    private IChatComponent footer;

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;listFormattedStringToWidth(Ljava/lang/String;I)Ljava/util/List;", ordinal = 1))
    public List<String> renderPlayerlist(FontRenderer instance, String str, int wrapWidth){

        List<String> footer = new ArrayList<>(instance.listFormattedStringToWidth(str, wrapWidth));
        if (ZombiesExplorer.MobSpawnOrder) {
            List<String> orderList = new ArrayList<>(Order.orderList);
            if (!orderList.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int z = 0; z < orderList.size(); z++) {
                    String s = orderList.get(z);
                    stringBuilder.append(s);
                    if (z != orderList.size() - 1) {
                        stringBuilder.append(EnumChatFormatting.GRAY);
                        stringBuilder.append(" -> ");
                    }
                }
                footer.add(stringBuilder.toString());
            }
        }

        return footer;
    }
}
