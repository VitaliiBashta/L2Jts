package org.mmocore.gameserver.data.xml.holder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.mmocore.commons.data.AbstractHolder;

/**
 * Create by Mangol on 24.12.2015.
 */
public class RecommendationHolder extends AbstractHolder {
    private static final RecommendationHolder INSTANCE = new RecommendationHolder();
    private final Table<Integer, Integer, Integer> recTable = HashBasedTable.create();


    public static RecommendationHolder getInstance() {
        return INSTANCE;
    }

    public void addRecommendation(final int level, final int recNumber, final int recBonus) {
        if (recTable.containsRow(level)) {
            recTable.row(level).put(recNumber, recBonus);
        } else {
            recTable.put(level, recNumber, recBonus);
        }
    }

    public int getBonusRecommendation(final int level, final int recNumber) {
        if (!recTable.containsRow(level)) {
            error("recommendation no contains level - " + level);
            return 1;
        } else if (!recTable.row(level).containsKey(recNumber)) {
            error("recommendation level - " + level + " no contains recNumber - " + recNumber);
            return 1;
        }
        return recTable.row(level).get(recNumber);
    }

    @Override
    public int size() {
        return recTable.rowKeySet().size();
    }

    @Override
    public void clear() {
        recTable.clear();
    }
}
