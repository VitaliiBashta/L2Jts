package org.jts.dataparser.data.holder.setting;

import org.jts.dataparser.data.annotations.array.StringArray;

/**
 * @author : Camelion
 * @date : 23.08.12 2:23
 * <p/>
 * Содержит в себе информацию о настройках героев
 */
public class HeroGeneralSetting {
    // Список скилов, выдаваемых герою
    @StringArray(canBeNull = false)
    public String[] hero_skill;
}
