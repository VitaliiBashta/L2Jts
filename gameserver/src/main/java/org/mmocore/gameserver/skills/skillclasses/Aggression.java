package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

public class Aggression extends Skill {
    private final boolean _unaggring;
    private final boolean _silent;

    public Aggression(final StatsSet set) {
        super(set);
        _unaggring = set.getBool("unaggroing", false);
        _silent = set.getBool("silent", false);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        int effect = _effectPoint;

        if (isSSPossible() && (activeChar.getChargedSoulShot() || activeChar.getChargedSpiritShot() > 0)) {
            effect *= 2;
        }

        for (final Creature target : targets) {
            if (target != null) {
                if (!target.isAutoAttackable(activeChar)) {
                    continue;
                }
                if (target.isNpc()) {
                    if (_unaggring) {
                        if (target.isNpc() && activeChar.isPlayable()) {
                            ((NpcInstance) target).getAggroList().addDamageHate(activeChar, 0, -effect);
                        }
                    } else {
                        target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, effect);
                        if (!_silent) {
                            target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar, null, 0);
                        }
                    }
                } else if (target.isPlayable() && !target.isDebuffImmune()) {
                    target.setTarget(activeChar);
                }
                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}