package org.mmocore.gameserver.utils;


import org.mmocore.gameserver.model.Effect;

import java.util.Comparator;


/**
 * Сортирует эффекты по группам для корректного отображения в клиенте: включаемые, танцы/песни, положительные/отрицательные
 *
 * @author G1ta0
 */
public class EffectsComparator implements Comparator<Effect> {
    private static final EffectsComparator instance = new EffectsComparator(1, -1);
    private static final EffectsComparator reverse = new EffectsComparator(-1, 1);

    private final int _greater;
    private final int _smaller;

    private EffectsComparator(final int g, final int s) {
        _greater = g;
        _smaller = s;
    }

    public static EffectsComparator getInstance() {
        return instance;
    }

    public static EffectsComparator getReverseInstance() {
        return reverse;
    }

    @Override
    public int compare(final Effect e1, final Effect e2) {
        final boolean toggle1 = e1.getSkill().getTemplate().isToggle();
        final boolean toggle2 = e2.getSkill().getTemplate().isToggle();

        if (toggle1 && toggle2)
            return compareStartTime(e1, e2);

        if (toggle1 || toggle2) {
            if (toggle1)
                return _greater;
            else
                return _smaller;
        }

        final boolean music1 = e1.getSkill().getTemplate().isMusic();
        final boolean music2 = e2.getSkill().getTemplate().isMusic();

        if (music1 && music2)
            return compareStartTime(e1, e2);

        if (music1 || music2) {
            if (music1)
                return _greater;
            else
                return _smaller;
        }

        final boolean offensive1 = e1.isOffensive();
        final boolean offensive2 = e2.isOffensive();

        if (offensive1 && offensive2)
            return compareStartTime(e1, e2);

        if (offensive1 || offensive2) {
            if (!offensive1)
                return _greater;
            else
                return _smaller;
        }

        final boolean trigger1 = e1.getSkill().getTemplate().isTrigger();
        final boolean trigger2 = e2.getSkill().getTemplate().isTrigger();

        if (trigger1 && trigger2)
            return compareStartTime(e1, e2);

        if (trigger1 || trigger2) {
            if (trigger1)
                return _greater;
            else
                return _smaller;
        }

        return compareStartTime(e1, e2);
    }

    private int compareStartTime(final Effect o1, final Effect o2) {
        if (o1.getStartTime() > o2.getStartTime())
            return _greater;

        if (o1.getStartTime() < o2.getStartTime())
            return _smaller;

        return 0;
    }
}