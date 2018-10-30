package org.mmocore.gameserver.object.components.player.recomendation;

import org.mmocore.gameserver.data.xml.holder.RecommendationHolder;
import org.mmocore.gameserver.object.Player;

public final class RecomBonus {
    private static final RecommendationHolder recommendationHolder = RecommendationHolder.getInstance();

    public static int getRecoBonus(final Player activeChar) {
        if (activeChar != null && activeChar.isOnline()) {
            if (activeChar.getRecommendationComponent().getRecomHave() == 0) {
                return 0;
            }
            return recommendationHolder.getBonusRecommendation(activeChar.getLevel(), Math.min(100, activeChar.getRecommendationComponent().getRecomHave()));
        }
        return 0;
    }

    public static double getRecoMultiplier(Player activeChar) {
        double bonus = getRecoBonus(activeChar);
        if (bonus > 0) {
            return 1. + bonus / 100;
        } else {
            return 1.;
        }
    }
}