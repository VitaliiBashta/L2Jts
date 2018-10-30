package org.mmocore.gameserver.model.reward;


import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @reworked VISTALL
 */
@SuppressWarnings("serial")
public class RewardList extends ArrayList<RewardGroup> {
    public static final int MAX_CHANCE = 1000000;
    private final RewardType _type;
    private final boolean _autoLoot;

    public RewardList(final RewardType rewardType, final boolean a) {
        super(5);
        _type = rewardType;
        _autoLoot = a;
    }

    public List<RewardItem> roll(final Player player) {
        return roll(player, 1.0, false, false);
    }

    public List<RewardItem> rollEvent() {
        final List<RewardItem> temp = new ArrayList<>(size());
        for (final RewardGroup g : this) {
            final List<RewardItem> tdl = g.rollItems(1.0, 1.0, 1.0);
            if (!tdl.isEmpty())
                temp.addAll(tdl);
        }
        return temp;
    }

    public List<RewardItem> roll(final Player player, final double mod) {
        return roll(player, mod, false, false);
    }

    public List<RewardItem> roll(final Player player, final double mod, final boolean isRaid) {
        return roll(player, mod, isRaid, false);
    }

    public List<RewardItem> roll(final Player player, final double mod, final boolean isRaid, final boolean isSiegeGuard) {
        final List<RewardItem> temp = new ArrayList<>(size());
        for (final RewardGroup g : this) {
            final List<RewardItem> tdl = g.roll(_type, player, mod, isRaid, isSiegeGuard);
            if (!tdl.isEmpty())
                temp.addAll(tdl);
        }
        return temp;
    }

    public boolean validate() {
        for (final RewardGroup g : this) {
            double chanceSum = 0; // сумма шансов группы
            for (final RewardData d : g.getItems()) {
                chanceSum += d.getChance();
            }
            if (chanceSum <= MAX_CHANCE) // всё в порядке?
            {
                return true;
            }
            final double mod = MAX_CHANCE / chanceSum;
            for (final RewardData d : g.getItems()) {
                final double chance = d.getChance() * mod; // коррекция шанса группы
                d.setChance(chance);
                g.setChance(MAX_CHANCE);
            }
        }
        return false;
    }

    public boolean isAutoLoot() {
        return _autoLoot;
    }

    public RewardType getType() {
        return _type;
    }
}