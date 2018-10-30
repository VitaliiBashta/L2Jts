package org.mmocore.gameserver.templates.custom.community.leaseTransform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mangol
 * @since 03.02.2016
 */
public class LeaseTransformTemplate {
    private final Map<Integer, TimesTemplate> times = new HashMap<>();
    private final String descriptionRu;
    private final String descriptionEn;
    private int id;
    private String nameRu;
    private String nameEn;
    private int minLevel;
    private int maxLevel;
    private int skillId;
    private int skillLevel;
    private Optional<BonusTemplate> bonus = Optional.empty();

    public LeaseTransformTemplate(final String descriptionRu, final String descriptionEn) {
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
    }

    public LeaseTransformTemplate(final int id, final String nameRu, final String nameEn, final int minLevel, final int maxLevel, final int skillId, final int skillLevel, final String descriptionRu, final String descriptionEn) {
        this.id = id;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public String getNameEn() {
        return nameEn;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public String getDescriptionEn() {
        return descriptionEn;

    }

    public Collection<TimesTemplate> getTimesValue() {
        return times.values();
    }

    public Map<Integer, TimesTemplate> getTimes() {
        return times;
    }

    public Optional<TimesTemplate> getTimes(final int key) {
        return Optional.ofNullable(times.get(key));
    }

    public void addTimes(final TimesTemplate t) {
        times.put(t.getKey(), t);
    }

    public int getId() {
        return id;
    }

    public Optional<BonusTemplate> getBonus() {
        return bonus;
    }

    public void setBonus(final Optional<BonusTemplate> bonus) {
        this.bonus = bonus;
    }
}
