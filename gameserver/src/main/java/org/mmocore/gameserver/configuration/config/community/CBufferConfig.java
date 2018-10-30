package org.mmocore.gameserver.configuration.config.community;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 13.12.2015.
 */
@Configuration("community/communityBuffer.json")
public class CBufferConfig {
    public static boolean enableBuffer;
    public static int[] price;
    public static boolean priceMod;
    public static int saveId;
    public static long savePrice;
    public static int time;
    public static int minLevel;
    public static int maxLevel;
    public static int freeLevel;
    @Setting(splitter = ",")
    public static int[] allowedBuffs;

    public static boolean recover;
    public static boolean clear;
    public static boolean peaceRecover;
    public static int[] recoverPrice;
    public static boolean premiumMod;
    public static double paMod;
    public static boolean inSiege;
    public static boolean inPvP;
    public static boolean inBattle;
    public static boolean onEvents;
    public static boolean recoverPvPFlag;
    public static boolean recoverBattle;
    public static int rejectAfterDeathSec;
    public static boolean onTvt;
    public static boolean onCtf;
}
