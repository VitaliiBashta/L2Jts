package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.scripts.bosses.BelethManager;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.world.World;

/**
 * @author pchayka
 */

public class Beleth extends Mystic {
    private static final int CLONE = 29119;
    private long _lastFactionNotifyTime = 0;

    public Beleth(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        BelethManager.setBelethDead();
        AnnouncementUtils.announceBossDeath(getActor());
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();

        if (System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval) {
            _lastFactionNotifyTime = System.currentTimeMillis();

            for (NpcInstance npc : World.getAroundNpc(actor)) {
                if (npc.getNpcId() == CLONE) {
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
                }
            }
        }

        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean canSeeInSilentMove(Playable target) {
        return true;
    }

    @Override
    public boolean canSeeInHide(Playable target) {
        return true;
    }

    @Override
    public void addTaskAttack(Creature target) {
        return;
    }

}