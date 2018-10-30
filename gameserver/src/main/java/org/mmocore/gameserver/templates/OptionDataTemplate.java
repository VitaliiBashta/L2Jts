package org.mmocore.gameserver.templates;


import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.StatTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 19:17/19.05.2011
 */
public class OptionDataTemplate extends StatTemplate {
    private final List<SkillEntry> _skills = new ArrayList<>(0);
    private final int _id;

    public OptionDataTemplate(final int id) {
        _id = id;
    }

    public void addSkill(final SkillEntry skill) {
        _skills.add(skill);
    }

    public List<SkillEntry> getSkills() {
        return _skills;
    }

    public int getId() {
        return _id;
    }
}
