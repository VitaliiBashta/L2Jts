package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.manager.naia.NaiaCoreManager;
import org.mmocore.gameserver.manager.naia.NaiaTowerManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author pchayka
 */
public class MutatedElpy extends Fighter {
    public MutatedElpy(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NaiaCoreManager.launchNaiaCore();
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();

        if (!attacker.isPlayable() || !NaiaTowerManager.isValidPlayer(attacker.getPlayer())) {
            if (actor.isDead())
                actor.stopDecay();

            actor.setCurrentHpMp(actor.getMaxHp(), actor.getMaxMp(), true);
            actor.setCurrentCp(actor.getMaxCp());
            return;
        }

        actor.doDie(attacker);
    }

}