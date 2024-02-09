package com.seosean.zombiesexplorer;

import com.google.common.collect.Sets;
import com.seosean.zombiesexplorer.utils.DebugUtils;
import com.seosean.zombiesexplorer.utils.GameUtils;
import com.seosean.zombiesexplorer.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PowerUpDetect {
    public PowerUpDetect(){

    }


    public static Integer[] r2MaxRoundsDE = {2, 8, 12, 16, 21, 26};
    public static Integer[] r2MaxRoundsBB = {2, 5, 8, 12, 16, 21, 26};
    public static Integer[] r3MaxRoundsDEBB = {3, 6, 9, 13, 17, 22, 27};

    public static Integer[] r2MaxRoundsAA = {2, 5, 8, 12, 16, 21, 26, 31, 36, 41, 46, 51, 61, 66, 71, 76, 81, 86, 91, 96};
    public static Integer[] r3MaxRoundsAA = {3, 6, 9, 13, 17, 22, 27, 32, 37, 42, 47, 52, 62, 67, 72, 77, 82, 87, 92, 97};

    public static Integer[] r2InsRoundsDE = {2, 8, 11, 14, 17, 23};
    public static Integer[] r2InsRoundsBB = {2, 5, 8, 11, 14, 17, 23};
    public static Integer[] r3InsRoundsDEBB = {3, 6, 9, 12, 18, 21, 24};

    public static Integer[] r2InsRoundsAA = {2, 5, 8, 11, 14, 17, 20, 23};
    public static Integer[] r3InsRoundsAA = {3, 6, 9, 12, 15, 18, 21};

    public static Integer[] r5SSRoundsAA = {5, 15, 45, 55, 65, 75, 85, 95, 105};
    public static Integer[] r6SSRoundsAA = {6, 16, 26, 36, 46, 66, 76, 86, 96};
    public static Integer[] r7SSRoundsAA = {7, 17, 27, 37, 47, 67, 77, 87, 97};
    private static final Set<String> ALL_IN_ALL_LANGUAGES =  Sets.newHashSet("INSTA KILL", "瞬间击杀", "一擊必殺", "MAX AMMO", "弹药满载", "填滿彈藥", "DOUBLE GOLD", "双倍金钱", "雙倍金幣", "SHOPPING SPREE", "购物狂潮", "購物狂潮", "BONUS GOLD", "金钱加成", "額外金幣", "CARPENTER", "木匠", "木匠");

    public List<Integer> maxRounds = new ArrayList<>();
    public List<Integer> insRounds = new ArrayList<>();
    public List<Integer> ssRounds = new ArrayList<>();

    public void iniPowerupPatterns(){
        insRounds.clear();
        maxRounds.clear();
        ssRounds.clear();
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText().replaceAll(ZombiesExplorer.EMOJI_REGEX, "").replaceAll(ZombiesExplorer.COLOR_REGEX, "");
        if(message.contains(":")) {
            return;
        }

        if(message.contains("Fight with your teammates against oncoming")){
            this.iniPowerupPatterns();
        }

        if(!ZombiesExplorer.PowerupDetector){
            return;
        }

        if (GameUtils.isInZombies()){
            if(!message.contains("activated") && !message.contains("激活") && !message.contains("啟用")) {
                return;
            }
            int round = ZombiesExplorer.getInstance().getPowerUpDetect().getCurrentRound();

            if (round == 0) {
                return;
            }

            if(message.contains("Insta Kill") || message.contains("瞬间") || message.contains("一擊")){
                if(insRounds.isEmpty() && round == 2){
                    this.setInstaRound(2);
                }else if(insRounds.isEmpty() && round == 3 && gameTick <= 9999){
                    this.setInstaRound(2);
                }else if(insRounds.isEmpty() && round == 3){
                    this.setInstaRound(3);
                }else if(round == 4 && insRounds.isEmpty()){
                    this.setInstaRound(3);
                }
            }else if(message.contains("Max Ammo") || message.contains("弹药满载") || message.contains("填滿彈藥")){
                if(maxRounds.isEmpty() && round == 2){
                    this.setMaxRound(2);
                }else if(maxRounds.isEmpty() && round == 3 && gameTick <= 9999){
                    this.setMaxRound(2);
                }else if(maxRounds.isEmpty() && gameTick > 9999 && round == 3){
                    this.setMaxRound(3);
                }else if(round == 4 && maxRounds.isEmpty()){
                    this.setMaxRound(3);
                }
            }else if(message.contains("Shopping Spree") || message.contains("狂潮")){
                if(ssRounds.isEmpty() && round == 5){
                    this.setSSRound(5);
                }else if(ssRounds.isEmpty() && round == 6 && gameTick <= 9999){
                    this.setSSRound(5);
                }else if(ssRounds.isEmpty() && round == 6){
                    this.setSSRound(6);
                }else if(ssRounds.isEmpty() && round == 7 && gameTick <= 9999){
                    this.setSSRound(6);
                }else if(ssRounds.isEmpty() && round == 7){
                    this.setSSRound(7);
                }else if(ssRounds.isEmpty() && round == 8){
                    this.setSSRound(7);
                }
            }
        }
    }

    private boolean isGameStart = false;
    private int gameTick = 0;
    @SubscribeEvent
    public void timerW1End(TickEvent.ClientTickEvent event){
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.isSingleplayer()) {
            return;
        }
        if (event.phase!= TickEvent.Phase.START) {
            return;
        }
        EntityPlayerSP p = mc.thePlayer;
        if (p==null) {
            return;
        }
        if (isGameStart) {
            gameTick += 50;
        }
    }

    public int getGameTick() {
        return gameTick;
    }

    public void setInstaRound(int round) {
        GameUtils.ZombiesMap map = GameUtils.getMap();
        switch (map) {
            case DEAD_END:
                insRounds = Arrays.asList(round == 2 ? r2InsRoundsDE : r3InsRoundsDEBB);
                break;
            case BAD_BLOOD:
                insRounds = Arrays.asList(round == 2 ? r2InsRoundsBB : r3InsRoundsDEBB);
                break;
            case ALIEN_ARCADIUM:
                insRounds = Arrays.asList(round == 2 ? r2InsRoundsAA : r3InsRoundsAA);
                break;
        }
    }

    public void setMaxRound(int round) {
        GameUtils.ZombiesMap map = GameUtils.getMap();
        switch (map) {
            case DEAD_END:
                maxRounds = Arrays.asList(round == 2 ? r2MaxRoundsDE : r3MaxRoundsDEBB);
                break;
            case BAD_BLOOD:
                maxRounds = Arrays.asList(round == 2 ? r2MaxRoundsBB : r3MaxRoundsDEBB);
                break;
            case ALIEN_ARCADIUM:
                maxRounds = Arrays.asList(round == 2 ? r2MaxRoundsAA : r3MaxRoundsAA);
                break;
        }
    }

    public void setSSRound(int round) {
        switch (round) {
            case 5:
                ssRounds = Arrays.asList(r5SSRoundsAA);
                break;
            case 6:
                ssRounds = Arrays.asList(r6SSRoundsAA);
                break;
            case 7:
                ssRounds = Arrays.asList(r7SSRoundsAA);
                break;
        }
    }

    private static List<String> ROUND = Arrays.asList("回合", ". kolo", "Runde", "Ronde", "Kierros", "Manche", "Runde", "Γύρος", ". Kör", "Round", "ラウンド", "라운드", "Runde", "Runda", "Horda", "Rodada", "Runda", "Раунд", "Ronda", "Runda", ". Tur", "Раунд");

    private int currentRound;
    public int getCurrentRound() {
        return currentRound;
    }

    public void detectNextPowerupRound(String title) {
        if(!GameUtils.isInZombies()){
            return;
        }

        if(title == null){
            return;
        }

        if(title.isEmpty()){
            return;
        }

        title = StringUtils.trim(title);

        for (String round : ROUND) {
            if (title.contains(round)) {
                String regex = "\\d+";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(title);
                if (matcher.find()) {
                    currentRound = Integer.parseInt(matcher.group());
                }
                break;
            }
        }

        if (currentRound == 0) {
            return;
        }

        List<Integer> rounds = new ArrayList<>();
        rounds.addAll(insRounds);
        rounds.addAll(maxRounds);
        rounds.addAll(ssRounds);

        int amountOfPowerups = 0;
        for (int round : rounds) {
            if (round == currentRound) {
                amountOfPowerups ++;
            }
        }

        this.amountOfIncomingPowerups = amountOfPowerups;
    }

    public int amountOfIncomingPowerups;

    public void setGameStart(boolean flag) {
        this.isGameStart = flag;
    }
}
