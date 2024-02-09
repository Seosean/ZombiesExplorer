package com.seosean.zombiesexplorer.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ZombiesExplorerGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraft) {

    }

    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ZombiesExplorerGuiConfig.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
}
