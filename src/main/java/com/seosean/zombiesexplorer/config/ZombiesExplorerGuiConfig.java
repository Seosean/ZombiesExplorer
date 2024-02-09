package com.seosean.zombiesexplorer.config;

import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ZombiesExplorerGuiConfig extends GuiConfig {
    public ZombiesExplorerGuiConfig(GuiScreen parent) {
        super(parent,
                new ConfigElement
                        (ZombiesExplorer
                                .getInstance()
                                .getConfig()
                                .getCategory(Configuration.CATEGORY_GENERAL))
                        .getChildElements(),
                ZombiesExplorer.MODID,
                false,
                false,
                "ZombiesExplorer Configuration"
        );
    }
}
