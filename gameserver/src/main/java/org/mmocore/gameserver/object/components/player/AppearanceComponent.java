package org.mmocore.gameserver.object.components.player;

import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

import java.awt.*;

/**
 * @author Mangol
 * @since 19.02.2016
 */
public class AppearanceComponent {
    public static final int DEFAULT_TITLE_COLOR = 0xFFFF77;
    public static final int DEFAULT_NAME_COLOR = 0xFFFFFF;
    private final Player player;
    private int face, hairStyle, hairColor;
    private int nameColor, titleColor;

    public AppearanceComponent(final Player player) {
        this.player = player;
    }

    /**
     * @param titleColor - Цвет титула.
     * @param variables  - Если это кастомный титул, нужно записывать в вариаблесы.
     */
    public void setTitleColor(final int titleColor, final boolean variables) {
        if (variables) {
            if (titleColor != DEFAULT_TITLE_COLOR) {
                player.getPlayerVariables().set(PlayerVariables.TITLE_COLOR, Integer.toHexString(titleColor), -1);
            } else {
                player.getPlayerVariables().remove(PlayerVariables.TITLE_COLOR);
            }
        }
        this.titleColor = titleColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    /**
     * Автоматически использует вариаблесы, и записывает переменную.
     *
     * @param titleColor - Цвет титула.
     */
    public void setTitleColor(final int titleColor) {
        setTitleColor(titleColor, true);
    }

    /**
     * @param nameColor - Цвет имени.
     * @param variables - Если это кастомный титул, нужно записывать в вариаблесы.
     */
    public void setNameColor(final int nameColor, final boolean variables) {
        if (variables) {
            if (nameColor != OtherConfig.NORMAL_NAME_COLOUR && nameColor != OtherConfig.CLANLEADER_NAME_COLOUR && nameColor != OtherConfig.GM_NAME_COLOUR && nameColor != ServicesConfig.SERVICES_OFFLINE_TRADE_NAME_COLOR) {
                player.getPlayerVariables().set(PlayerVariables.NAME_COLOR, Integer.toHexString(nameColor), -1);
            } else if (nameColor == OtherConfig.NORMAL_NAME_COLOUR) {
                player.getPlayerVariables().remove(PlayerVariables.NAME_COLOR);
            }
        }
        this.nameColor = nameColor;
    }

    public int getNameColor() {
        if (player.isInObserverMode()) {
            return Color.black.getRGB();
        }
        return nameColor;
    }

    /**
     * Автоматически использует вариаблесы, и записывает переменную.
     *
     * @param nameColor - Цвет имени.
     */
    public void setNameColor(final int nameColor) {
        setNameColor(nameColor, true);
    }

    /**
     * @return - Возвращаем тип лица.
     */
    public int getFace() {
        return this.face;
    }

    /**
     * @param face - Задаем тип лица
     */
    public void setFace(final int face) {
        this.face = face;
    }

    /**
     * @return - Возвращаем цвет волос.
     */
    public int getHairColor() {
        return this.hairColor;
    }

    /**
     * @param hairColor - Задаем цвет волос
     */
    public void setHairColor(final int hairColor) {
        this.hairColor = hairColor;
    }

    /**
     * @return - Возвращаем тип волос
     */
    public int getHairStyle() {
        return this.hairStyle;
    }

    /**
     * @param hairStyle - задаем тип волос
     */
    public void setHairStyle(final int hairStyle) {
        this.hairStyle = hairStyle;
    }
}
