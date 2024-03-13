package com.seosean.zombiesexplorer;

import com.seosean.showspawntime.ShowSpawnTime;
import com.seosean.showspawntime.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class PowerUpDetect {

    public static void detectNextPowerupRound() {
        if(!PlayerUtils.isInZombies()){
            return;
        }

        int currentRound = ShowSpawnTime.getSpawnTimes().currentRound;

        if (currentRound == 0) {
            return;
        }

        List<Integer> rounds = new ArrayList<>();
        rounds.addAll(ShowSpawnTime.getPowerupDetect().insRounds);
        rounds.addAll(ShowSpawnTime.getPowerupDetect().maxRounds);
        rounds.addAll(ShowSpawnTime.getPowerupDetect().ssRounds);

        int amountOfPowerups = 0;
        for (int round : rounds) {
            if (round == currentRound) {
                amountOfPowerups ++;
            }
        }

        amountOfIncomingPowerups = amountOfPowerups;
    }

    public static int amountOfIncomingPowerups;
}
