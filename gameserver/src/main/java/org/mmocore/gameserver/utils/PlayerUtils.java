package org.mmocore.gameserver.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.object.Player;

/**
 * @author Java-man
 */
public final class PlayerUtils {
    private PlayerUtils() {
    }

    public static boolean canPlayerWearDye(final Player player, final DyeData dye) {
        final int classId = player.getPlayerClassComponent().getActiveClassId();
        return ArrayUtils.contains(dye.wear_class, classId);
    }

    /**
     * Return PenaltyModifier (can use in all cases)
     *
     * @param count    - how many times <percents> will be substructed
     * @param percents - percents to substruct
     * @author Styx
     */

    /*
     *  This is for fine view only ;)
     *
     *	public final static double penaltyModifier(int count, int percents)
     *	{
     *		int allPercents = 100;
     *		int allSubstructedPercents = count * percents;
     *		int penaltyInPercents = allPercents - allSubstructedPercents;
     *		double penalty = penaltyInPercents / 100.0;
     *		return penalty;
     *	}
     */
    public static double penaltyModifier(final long count, final double percents) {
        return Math.max(1. - count * percents / 100, 0);
    }

    /**
     * Максимальный достижимый уровень чара
     */
    public static int getMaxLevel() {
        return AllSettingsConfig.ALT_MAX_LEVEL;
    }

    /**
     * Максимальный уровень для саба
     */
    public static int getMaxSubLevel() {
        return AllSettingsConfig.ALT_MAX_SUB_LEVEL;
    }

    /**
     * Максимальный уровень пета
     */
    public static int getPetMaxLevel() {
        return 86;
    }
}