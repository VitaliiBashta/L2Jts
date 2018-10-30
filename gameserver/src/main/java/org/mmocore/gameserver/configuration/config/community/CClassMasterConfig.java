package org.mmocore.gameserver.configuration.config.community;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Mangol on 14.12.2015.
 */
@Configuration("community/communityClassMaster.json")
public class CClassMasterConfig {
    public static boolean allowClassMaster;
    @Setting(splitter = ",", method = "classLevel")
    public static List<Integer> classLevel = new ArrayList<>();
    @Setting(splitter = ",")
    public static int[] itemId = new int[]{57, 57, 57};
    @Setting(splitter = ",")
    public static long[] priceCount = new long[]{1000, 1000, 1000};
    @Setting(splitter = ",")
    public static int[] secondItem = new int[]{57, 57, 57};
    @Setting(splitter = ",")
    public static long[] secondPrice = new long[]{1000, 1000, 1000};
    public static boolean noble;
    public static boolean subAdd;
    @Setting(splitter = ",")
    public static long[] subAPrice;
    public static boolean subChange;
    @Setting(splitter = ",")
    public static long[] subCPrice;
    public static boolean subCancel;
    @Setting(splitter = ",")
    public static long[] subDPrice;

    private void classLevel(final int[] value) {
        for (final int cLevel : value) {
            if (cLevel != 0) {
                classLevel.add(cLevel);
            }
        }
    }
}
