package org.mmocore.gameserver.tables;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillsEngine;

public class SkillTable {
    private static final SkillTable INSTANCE = new SkillTable();

    private TIntObjectMap<SkillEntry> skills;
    private TIntIntHashMap maxLevelsTable;
    private TIntIntHashMap baseLevelsTable;

    public static SkillTable getInstance() {
        return INSTANCE;
    }

    public static int getSkillHashCode(final Skill skill) {
        return getSkillHashCode(skill.getId(), skill.getLevel());
    }

    public static int getSkillHashCode(final int skillId, final int skillLevel) {
        return skillId * 1000 + skillLevel;
    }

    public void load() {
        skills = SkillsEngine.getInstance().loadAllSkills();
        makeLevelsTable();
    }

    public void reload() {
        load();
    }

    public SkillEntry getSkillEntry(final int skillId, final int level) {
        return skills.get(getSkillHashCode(skillId, level));
    }

    public int getMaxLevel(final int skillId) {
        return maxLevelsTable.get(skillId);
    }

    public int getBaseLevel(final int skillId) {
        return baseLevelsTable.get(skillId);
    }

    private void makeLevelsTable() {
        maxLevelsTable = new TIntIntHashMap();
        baseLevelsTable = new TIntIntHashMap();
        for (final SkillEntry s : skills.valueCollection()) {
            final int skillId = s.getId();
            final int level = s.getLevel();
            final int maxLevel = maxLevelsTable.get(skillId);
            if (level > maxLevel) {
                maxLevelsTable.put(skillId, level);
            }
            if (baseLevelsTable.get(skillId) == 0) {
                baseLevelsTable.put(skillId, s.getTemplate().getBaseLevel());
            }
        }
    }
}