package org.mmocore.gameserver.scripts.ai.kamaloka;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * Минион босса 66й камалоки
 *
 * @author pchayka
 */
public class BiletteFollower extends Fighter {
    private final static long _skillInterval = 20000L;
    private static final SkillEntry s_npc_heal = SkillTable.getInstance().getSkillEntry(4065, 6);  // s_npc_heal6
    private final NpcInstance actor = getActor();
    private final Reflection r = actor.getReflection();
    private long _skillTimer = 0L;

    public BiletteFollower(NpcInstance actor) {
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
                if (npc.getNpcId() == 18573) {
                    boss = npc;
                }
            }

            if (boss != null) {
                actor.doCast(s_npc_heal, boss, false);
            }
            _skillTimer = System.currentTimeMillis();
        }
        super.thinkAttack();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (Rnd.chance(10)) {
            ChatUtils.say(actor, NpcString.ARG_THE_PAIN_IS_MORE_THAN_I_CAN_STAND);
        } else if (Rnd.chance(3)) {
            ChatUtils.say(actor, NpcString.AHH_HOW_DID_HE_FIND_MY_WEAKNESS);
        }
    }
}