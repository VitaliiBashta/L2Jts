package org.mmocore.gameserver.utils;

/**
 * @author VISTALL
 * @date 13:50/29.03.2011
 */
public enum Language {
    ENGLISH("en"),
    RUSSIAN("ru");

    public static final Language[] VALUES = Language.values();

    private final String _shortName;

    Language(final String shortName) {
        _shortName = shortName;
    }

    public String getShortName() {
        return _shortName;
    }
}
