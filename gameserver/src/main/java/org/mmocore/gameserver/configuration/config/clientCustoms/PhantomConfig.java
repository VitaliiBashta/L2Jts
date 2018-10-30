package org.mmocore.gameserver.configuration.config.clientCustoms;

import org.mmocore.commons.configuration.annotations.Configuration;

/**
 * Created by Hack
 * Date: 21.08.2016 7:59
 */
@Configuration("phantoms.json")
public class PhantomConfig {
    public static boolean allowPhantoms;
    public static int firstWaveDelay;
    public static int waveRespawn;
    public static int[] phantomSpawnDelayMinMax;
    public static int[] phantomDespawnDelayMinMax;
    public static boolean everybodyMaxLevel;
    public static int minEnchant;
    public static int maxEnchant;
    public static double enchantChance;
    public static long townAiTick;
    public static long townAiInit;
    public static long chatAnswerDelay;
    public static double chatAnswerChance;
    public static int randomMoveDistance;
    public static double randomMoveChance;
    public static int[] userActions;
    public static double userActionChance;
    public static int moveToNpcRange;
    public static double moveToNpcChance;
}
