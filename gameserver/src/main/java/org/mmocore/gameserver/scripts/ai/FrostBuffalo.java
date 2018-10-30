package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

/**
 * AI моба Frost Buffalo для Frozen Labyrinth.<br>
 * - Если был атакован физическим скилом, спавнится миньон-мобы Lost Buffalo 22093 в количестве 4 штук.<br>
 * - Не используют функцию Random Walk, если были заспавнены "миньоны"<br>
 *
 * @author SYS
 */
public class FrostBuffalo extends Fighter {
    private static final int MOBS = 22093;
    private static final int MOBS_COUNT = 4;
    private boolean _mobsNotSpawned = true;

    public FrostBuffalo(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        NpcInstance actor = getActor();
        if (skill.getTemplate().isMagic()) {
            return;
        }
        if (_mobsNotSpawned) {
            _mobsNotSpawned = false;
            for (int i = 0; i < MOBS_COUNT; i++) {
                try {
                    SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(MOBS));
                    sp.setLoc(Location.findPointToStay(actor, 100, 120));
                    NpcInstance npc = sp.doSpawn(true);
                    if (caster.isPet() || caster.isSummon()) {
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster, Rnd.get(2, 100));
                    }
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster.getPlayer(), Rnd.get(1, 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        _mobsNotSpawned = true;
        super.onEvtDead(killer);
    }

    @Override
    protected boolean randomWalk() {
        return _mobsNotSpawned;
    }
}