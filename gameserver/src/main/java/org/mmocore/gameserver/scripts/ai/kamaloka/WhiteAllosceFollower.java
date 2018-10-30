package org.mmocore.gameserver.scripts.ai.kamaloka;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

/**
 * Минион босса 73й камалоки
 *
 * @author pchayka
 */
public class WhiteAllosceFollower extends Mystic {
    private final static long _skillInterval = 15000L;
    private static final SkillEntry s_soul_bind = SkillTable.getInstance().getSkillEntry(5624, 1);  // Soul Confinement
    private final NpcInstance actor = getActor();
    private long _skillTimer = 0L;

    public WhiteAllosceFollower(NpcInstance actor) {
        super(actor);
        actor.setIsInvul(true);
    }

    @Override
    protected boolean thinkActive() {
        if (_skillTimer + _skillInterval < System.currentTimeMillis()) {
            for (final Creature p : actor.getAroundCharacters(1000, 200)) {
                if (p.isPlayer() && !p.isDead() && !p.isInvisible()) {
                    actor.getAggroList().addDamageHate(p, 0, 10);
                }
            }

            final Creature target = actor.getAggroList().getRandomHated();
            if (target != null) {
                actor.doCast(s_soul_bind, target, false);
            }
            setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            addTaskMove(Location.findAroundPosition(actor, 400), false);
            _skillTimer = System.currentTimeMillis() + Rnd.get(1L, 5000L);
        }
        return super.thinkActive();
    }

    @Override
    protected void thinkAttack() {
        // do not attack
        setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }
}