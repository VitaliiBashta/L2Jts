package org.mmocore.gameserver.templates.custom.community;

import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 06.03.2016
 */
public class BuyClanSkillTemplate {
    private final int id;
    private final int level;
    private final int minLevel;
    private final int itemId;
    private final long itemCount;
    private final String icon;
    private final String nameRu;
    private final String nameEn;
    private final String descRu;
    private final String descEn;

    public BuyClanSkillTemplate(int id, int level, int minLevel, int itemId, long itemCount, String icon, String nameRu, String nameEn, String descRu, String descEn) {
        this.id = id;
        this.level = level;
        this.minLevel = minLevel;
        this.itemId = itemId;
        this.itemCount = itemCount;
        this.icon = icon;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.descRu = descRu;
        this.descEn = descEn;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getItemId() {
        return itemId;
    }

    public long getItemCount() {
        return itemCount;
    }

    public String getIcon() {
        return icon;
    }

    public String getName(final Player player) {
        switch (player.getLanguage()) {
            case RUSSIAN:
                return getNameRu();
            case ENGLISH:
                return getNameEn();
        }
        return "NONE";
    }

    public String getNameRu() {
        return nameRu;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getDesc(final Player player) {
        switch (player.getLanguage()) {
            case RUSSIAN:
                return getDescRu();
            case ENGLISH:
                return getDescEn();
        }
        return "NONE";
    }

    public String getDescRu() {
        return descRu;
    }

    public String getDescEn() {
        return descEn;
    }
}
