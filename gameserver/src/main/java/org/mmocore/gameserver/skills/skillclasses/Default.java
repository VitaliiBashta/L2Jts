package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Default extends Skill {
    private static final Logger _log = LoggerFactory.getLogger(Default.class);

    public Default(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (activeChar.isPlayer()) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.skillclasses.Default.NotImplemented").addNumber(getId()).addString(String.valueOf(getSkillType())));
        }
        _log.warn("NOTDONE skill: " + getId() + ", used by" + activeChar);
        activeChar.sendActionFailed();
    }
}
