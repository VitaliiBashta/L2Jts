package org.mmocore.gameserver.configuration.config.custom;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

import java.util.Arrays;
import java.util.List;

/**
 * Create by Mangol on 30.12.2015.
 */
@Configuration("custom/acp.json")
public class AcpConfig {
    public static boolean allowAcp;
    public static List<Integer> allowCpItemId = Arrays.asList(5592);
    public static List<Integer> allowHpItemId = Arrays.asList(1061, 1539, 1540, 14700);
    public static List<Integer> allowMpItemId = Arrays.asList(728, 22038);
    @Setting(increase = 1000, minValue = 1000, maxValue = 120000)
    public static int MAX_REUSE;
    @Setting(increase = 1000, minValue = 1000, maxValue = 120000)
    public static int MIN_REUSE;
    @Setting(minValue = 1, maxValue = 100)
    public static double cpPercent;
    @Setting(minValue = 1, maxValue = 100)
    public static double hpPercent;
    @Setting(minValue = 1, maxValue = 100)
    public static double mpPercent;
    public static int cpItemId;
    public static int hpItemId;
    public static int mpItemId;
    @Setting(increase = 1000, minValue = 1000)
    public static int reuseCp;
    @Setting(increase = 1000, minValue = 1000)
    public static int reuseHp;
    @Setting(increase = 1000, minValue = 1000)
    public static int reuseMp;
    public static boolean acpUseOlympiad;
    public static boolean acpUseEvent;
    public static boolean acpUseDuel;
    public static boolean acpUseSiege;
    public static boolean acpUseTransform;
    public static boolean acpUseWater;
    public static boolean acpUseInvisible;
    @Setting(increase = 1000, minValue = 1000)
    public static int reuseSmallCp;
    public static List<Integer> allowSmallCpItemId = Arrays.asList(5591);
    public static int smallCpItemId;
    @Setting(minValue = 1, maxValue = 100)
    public static double smallCpPercent;
    public static boolean acpUseInvul;
}
