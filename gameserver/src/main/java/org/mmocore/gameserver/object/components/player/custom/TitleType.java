package org.mmocore.gameserver.object.components.player.custom;

import org.apache.logging.log4j.util.Strings;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Language;

/**
 * @author Mangol
 * @since 08.10.2016
 */
public enum TitleType {
    EVENT("Event ^_^", "Event ^_^");
    public static final String DEFAULT = Strings.EMPTY;
    final String en;
    final String ru;

    TitleType(String en, String ru) {
        this.en = "[EN] " + en;
        this.ru = "[RU] " + ru;
    }

    public String getEn() {
        return en;
    }

    public String getRu() {
        return ru;
    }

    public String getLangTitle(final Player player) {
        if (player == null)
            return DEFAULT;
        return player.getLanguage() == Language.ENGLISH ? getEn() : getRu();
    }
}
