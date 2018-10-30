package org.mmocore.gameserver.configuration.config.custom;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * @author Mangol
 * @since 01.05.2016
 */
@Configuration("custom/custom.json")
public class CustomConfig {
    public static boolean subscriptionAllow;
    public static boolean buyPremiumBuySubscription;
    @Setting(minValue = 1., maxValue = 5)
    public static double cutSTR;
    @Setting(minValue = 1., maxValue = 5)
    public static double cutINT;
    @Setting(minValue = 1., maxValue = 5)
    public static double cutCON;
    @Setting(minValue = 1., maxValue = 5)
    public static double cutDEX;
    @Setting(minValue = 1., maxValue = 5)
    public static double cutWIT;
    @Setting(minValue = 1., maxValue = 5)
    public static double cutMEN;
    @Setting(minValue = 1., maxValue = 5)
    public static double cutRateXP;
    @Setting(minValue = 1., maxValue = 5)
    public static double cutRateSP;
    public static boolean blockСhatShout;
    public static boolean blockСhatTrade;
    public static int subscriptionHourBonus;
    public static boolean subscriptionRandomBonus;
    public static int subscriptionRandomHour;
    public static int subscriptionRandomHourAdd;
}
