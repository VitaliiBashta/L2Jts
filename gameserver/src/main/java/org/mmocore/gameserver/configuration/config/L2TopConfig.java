package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 28.11.2015.
 */
@Configuration("l2top.json")
public class L2TopConfig {
    @Setting(name = "MMOTopEnabled")
    public static boolean MMOTOP_ENABLED;

    @Setting(name = "MMOTopVoteFileURL", canNull = true)
    public static String MMOTOP_VOTE_FILE_URL = "";

    @Setting(name = "MMOTopBonusItemCount")
    public static int[] MMOTOP_BONUS_ITEM_COUNT;

    @Setting(name = "MMOTopParserDelay", increase = 1000)
    public static int MMOTOP_PARSER_DELAY;

    @Setting(name = "MMOTopCheckByIP")
    public static boolean MMOTOP_CHECH_BY_IP;

    public static int rewardItem;
    public static int rewardCount;
}
