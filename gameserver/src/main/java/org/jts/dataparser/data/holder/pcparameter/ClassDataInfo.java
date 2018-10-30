package org.jts.dataparser.data.holder.pcparameter;

import org.jts.dataparser.data.holder.pcparameter.bonus.LevelBonus;

/**
 * @author KilRoy
 */
public class ClassDataInfo {
    private LevelBonus hp;
    private LevelBonus mp;
    private LevelBonus cp;

    public ClassDataInfo(final LevelBonus hp, final LevelBonus mp, final LevelBonus cp) {
        this.hp = hp;
        this.mp = mp;
        this.cp = cp;
    }

    public LevelBonus getHp() {
        return hp;
    }

    public LevelBonus getMp() {
        return mp;
    }

    public LevelBonus getCp() {
        return cp;
    }
}