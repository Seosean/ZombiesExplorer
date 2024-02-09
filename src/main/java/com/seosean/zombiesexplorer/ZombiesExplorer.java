package com.seosean.zombiesexplorer;

import com.seosean.zombiesexplorer.commands.Commands;
import com.seosean.zombiesexplorer.config.ZombiesExplorerGuiConfig;
import com.seosean.zombiesexplorer.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@Mod(modid = ZombiesExplorer.MODID, version = ZombiesExplorer.VERSION,
        acceptedMinecraftVersions = "[1.8.9]",
        guiFactory = "com.seosean.zombiesexplorer.config.ZombiesExplorerGuiFactory"
)
public class ZombiesExplorer {
    public static final String MODID = "zombiesexplorer";
    public static final String VERSION = "1.1";
    public static ZombiesExplorer INSTANCE;
    public SpawnPatternNotice spawnPatternNotice;
    public PowerUpDetect powerUpDetect;
    private Configuration config;
    private Logger logger;

    public static final String EMOJI_REGEX = "(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)";

    public static final String COLOR_REGEX = "§[a-zA-Z0-9]";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        this.ConfigLoad();
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(spawnPatternNotice = new SpawnPatternNotice());
        MinecraftForge.EVENT_BUS.register(powerUpDetect = new PowerUpDetect());
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(keyToggleConfig);
        ClientCommandHandler.instance.registerCommand(new Commands());
    }

    public static ZombiesExplorer getInstance() {
        return INSTANCE;
    }

    public static boolean PowerupDetector;
    public static boolean BadHeadShotDetector;
    public static int PowerupPredictor;
    public static boolean NameTag;
    public static boolean BadHeadShotOnLine;
    public void ConfigLoad() {
        config.load();
        logger.info("Started loading config. ");

        String commentPowerupDetector;
        String commandBadHeadShotDetector;
        String commandPowerupPredictor;
        String commentNameTag;
        String commentBadHeadShotOnLine;

        commentPowerupDetector = "Powerup Detector";
        PowerupDetector = config.get(Configuration.CATEGORY_GENERAL, "Powerup Detector", true, commentPowerupDetector).getBoolean();

        commandBadHeadShotDetector = "Bad HeadShot Detector";
        BadHeadShotDetector = config.get(Configuration.CATEGORY_GENERAL, "Bad HeadShot Detector", true, commandBadHeadShotDetector).getBoolean();

        commandPowerupPredictor = "The amount of marks you want to apply for possible powerup zombie";
        PowerupPredictor = config.get(Configuration.CATEGORY_GENERAL, "Powerup Predictor", 1, commandPowerupPredictor, 0, 3).getInt();

        commentNameTag = "Show mark name tag for certain zombies, this is not an X-Ray feature.";
        NameTag = config.get(Configuration.CATEGORY_GENERAL, "Name Tag", true, commentNameTag).getBoolean();

        commentBadHeadShotOnLine = "Marks all mobs on the line connected to you and bad headshot mob.";
        BadHeadShotOnLine = config.get(Configuration.CATEGORY_GENERAL, "Bad HeadShot OnLine", true, commentBadHeadShotOnLine).getBoolean();

        config.save();
        logger.info("Finished loading config. ");
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ZombiesExplorer.MODID)) {
            config.save();
            ConfigLoad();
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public Logger logger()
    {
        return logger;
    }

    public SpawnPatternNotice getSpawnPatternNotice() {
        return spawnPatternNotice;
    }
    public PowerUpDetect getPowerUpDetect() {
        return powerUpDetect;
    }
    public KeyBinding keyToggleConfig = new KeyBinding("Config", Keyboard.KEY_K, "Zombies Explorer");

    @SubscribeEvent
    public void toggleGUI(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKey() == keyToggleConfig.getKeyCode() && Keyboard.getEventKeyState() && !Keyboard.isRepeatEvent() && Minecraft.getMinecraft().currentScreen == null) {
            new DelayedTask() {
                @Override
                public void run() {
                    Minecraft.getMinecraft().displayGuiScreen(new ZombiesExplorerGuiConfig(null));
                }
            }.runTaskLater(2);
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!event.world.isRemote) {
            return;
        }

        if (!event.entity.equals(Minecraft.getMinecraft().thePlayer)) {
            return;
        }

        ZombiesExplorer.getInstance().getPowerUpDetect().setGameStart(false);
    }

    public enum RenderType {
        BAD_HEADSHOT,
        DERIVED_BAD_HEADSHOT,
        POWERUP_PREDICT,
        POWERUP_ENSURED
    }
}
