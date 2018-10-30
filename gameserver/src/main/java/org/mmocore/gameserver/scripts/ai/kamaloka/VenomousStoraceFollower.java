package org.mmocore.gameserver.scripts.ai.kamaloka;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * Минион босса 63й камалоки
 *
 * @author pchayka
 */
public class VenomousStoraceFollower extends Fighter {
    private final static long _skillInterval = 20000L;
    private final NpcInstance actor = getActor();
    private final Reflection r = actor.getReflection();
    private long _skillTimer = 0L;

    public VenomousStoraceFollower(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void thinkAttack() {
        if (_skillTimer == 0) {
            _skillTimer = System.currentTimeMillis();
        }
        if (!r.isDefault() && _skillTimer + _skillInterval < System.currentTimeMillis()) {
            NpcInstance boss = null;
            for (final NpcInstance npc : r.getNpcs()) {
                if (npc.getNpcId() == 18571) {
                    boss = npc;
                }
            }

            if (boss != null) {
                if (boss.getCurrentHpPercents() < 70) {
                    boss.setCurrentHp(boss.getCurrentHp() + boss.getMaxHp() * 0.2, false);
                } else {
                    boss.setCurrentHp(boss.getMaxHp() - 10, false);
                }
                ChatUtils.say(actor, NpcString.THERES_NOT_MUCH_I_CAN_DO_BUT_I_WILL_RISK_MY_LIFE_TO_HELP_YOU);
            }
            actor.doDie(null);
        }
        super.thinkAttack();
    }
}