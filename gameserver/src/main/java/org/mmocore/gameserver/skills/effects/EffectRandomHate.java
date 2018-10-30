package org.mmocore.gameserver.skills.effects;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.npc.AggroList;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.List;


/**
 * @author VISTALL
 * @date 12:01/29.01.2011
 */
public class EffectRandomHate extends Effect {
    public EffectRandomHate(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        return getEffected().isMonster() && Rnd.chance(_template.chance(100));
    }

    @Override
    public void onStart() {
        final MonsterInstance monster = (MonsterInstance) getEffected();
        final Creature mostHated = monster.getAggroList().getMostHated();
        if (mostHated == null) {
            return;
        }

        final AggroList.AggroInfo mostAggroInfo = monster.getAggroList().get(mostHated);
        final List<Creature> hateList = monster.getAggroList().getHateList(monster.getAggroRange());
        hateList.remove(mostHated);

        if (!hateList.isEmpty()) {
            final AggroList.AggroInfo newAggroInfo = monster.getAggroList().get(hateList.get(Rnd.get(hateList.size())));
            final int oldHate = newAggroInfo.hate;

            newAggroInfo.hate = mostAggroInfo.hate;
            mostAggroInfo.hate = oldHate;
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
