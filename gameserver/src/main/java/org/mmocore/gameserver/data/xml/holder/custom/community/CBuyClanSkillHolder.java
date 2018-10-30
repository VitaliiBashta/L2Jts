package org.mmocore.gameserver.data.xml.holder.custom.community;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.custom.community.BuyClanSkillTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author Mangol
 * @since 06.03.2016
 */
public class CBuyClanSkillHolder extends AbstractHolder {
    private static final CBuyClanSkillHolder INSTANCE = new CBuyClanSkillHolder();
    private static final Map<Integer, BuyClanSkillTemplate> clanSkills = new TreeMap<>();

    public static CBuyClanSkillHolder getInstance() {
        return INSTANCE;
    }

    @Override
    public void clear() {
        clanSkills.clear();
    }

    public void addClanSkill(final BuyClanSkillTemplate buyClanSkillTemplate) {
        clanSkills.put(hash(buyClanSkillTemplate.getId(), buyClanSkillTemplate.getLevel()), buyClanSkillTemplate);
    }

    public Optional<BuyClanSkillTemplate> getClanSkill(final int id, final int level) {
        return Optional.ofNullable(clanSkills.get(hash(id, level)));
    }

    public Map<Integer, BuyClanSkillTemplate> getClanSkills() {
        return clanSkills;
    }

    public Collection<BuyClanSkillTemplate> getValuesClanSkills() {
        return clanSkills.values();
    }

    @Override
    public int size() {
        return clanSkills.size();
    }

    public int hash(final int id, final int level) {
        return id * 10000 + level;
    }
}
