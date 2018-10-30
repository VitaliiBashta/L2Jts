package org.mmocore.gameserver.templates.other;

import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 19.02.2016
 */
public class ColorTemplate {
    private final String color;
    private final String nameEn;
    private final String nameRu;

    public ColorTemplate(final String color, final String nameEn, final String nameRu) {
        this.color = color;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
    }

    public String getColor() {
        return color;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public String getName(final Player player) {
        switch (player.getLanguage()) {
            case RUSSIAN:
                return getNameRu();
            case ENGLISH:
                return getNameEn();
        }
        return "No Name";
    }
}
