package com.seosean.zombiesexplorer.utils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GameUtils {

    private static List<String> MAP_DEAD_END = Arrays.asList("穷途末路", "窮途末路", "Dead End");
    private static List<String> MAP_BAD_BLOOD = Arrays.asList("坏血之宫", "壞血宮殿", "Bad Blood");
    private static List<String> MAP_ALIEN_ARCADIUM = Arrays.asList("外星游乐园", "外星遊樂園", "Alien Arcadium");
    private static List<String> MAPS = Arrays.asList("穷途末路", "窮途末路", "Dead End", "坏血之宫", "壞血宮殿", "Bad Blood", "外星游乐园", "外星遊樂園", "Alien Arcadium");


    public static boolean isInZombies() {
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebarObjective == null) {
            return false;
        }
        String title = StringUtils.trim(sidebarObjective.getDisplayName());
        return StringUtils.ZOMBIES_TITLE.contains(title);
    }

    private static List<String> contents = new ArrayList<>();

    public static int parseSidebar() {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().theWorld == null) {
            return 0;
        }

        if (!isInZombies()) {
            return 0;
        }

        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebarObjective == null) {
            return 0;
        }

        Collection<Score> scores = scoreboard.getSortedScores(sidebarObjective);
        List<Score> filteredScores = scores.stream()
                .filter(p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        if (filteredScores.isEmpty()) {
            return 0;
        } else if (filteredScores.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(filteredScores, scores.size() - 15));
        } else {
            scores = filteredScores;
        }
        Collections.reverse(filteredScores);

        List<String> list = new ArrayList<>();
        for (Score line : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(line.getPlayerName());
            String scoreboardLine = ScorePlayerTeam.formatPlayerName(team, line.getPlayerName()).trim();
            list.add(scoreboardLine);
        }

        if (!list.isEmpty()) {
            contents = list;
        }

        String roundString = list.size() >= 3 ? list.get(2) : "";
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(roundString);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        return 0;
    }



    public static String getMapString() {
        GameUtils.parseSidebar();
        String mapString = StringUtils.trim(contents.size() < 4 ? "" : contents.get(contents.size() - 3));

        for (String regex : MAPS) {
            if (mapString.contains(regex)) {
                return regex;
            }
        }
        return "";
    }

    public static ZombiesMap getMap() {
        HashMap<String, List<String>> mapLists = new HashMap<>();
        mapLists.put("MAP_DEAD_END", MAP_DEAD_END);
        mapLists.put("MAP_BAD_BLOOD", MAP_BAD_BLOOD);
        mapLists.put("MAP_ALIEN_ARCADIUM", MAP_ALIEN_ARCADIUM);
        String mapName = GameUtils.getMapString();

        if (mapName.contains(":")) {
            mapName = StringUtils.trim(mapName.split(":")[1]);
        } else if (mapName.contains("：")) {
            mapName = StringUtils.trim(mapName.split("：")[1]);
        }

        for (Map.Entry<String, List<String>> entry : mapLists.entrySet()) {
            if (entry.getValue().contains(mapName)) {
                String listName = entry.getKey();
                switch (listName) {
                    case "MAP_DEAD_END": return ZombiesMap.DEAD_END;
                    case "MAP_BAD_BLOOD": return ZombiesMap.BAD_BLOOD;
                    case "MAP_ALIEN_ARCADIUM": return ZombiesMap.ALIEN_ARCADIUM;
                }
                break;
            }
        }
        return ZombiesMap.NULL;
    }

    public enum ZombiesMap {
        NULL,
        DEAD_END,
        BAD_BLOOD,
        ALIEN_ARCADIUM;
    }

}
