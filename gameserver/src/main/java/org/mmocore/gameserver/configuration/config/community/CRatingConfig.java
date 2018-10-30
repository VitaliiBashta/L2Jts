package org.mmocore.gameserver.configuration.config.community;

import org.mmocore.commons.configuration.annotations.Configuration;

/**
 * @author Mangol
 * @since 30.01.2016
 */
@Configuration("community/communityRating.json")
public class CRatingConfig {
    public static boolean AllowTopPvp;
    public static boolean AllowTopPk;
    public static boolean AllowTopOnline;
    public static boolean AllowTopFortune;
}
