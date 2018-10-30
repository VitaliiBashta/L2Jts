package org.mmocore.gameserver.scripts.ai.freya;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.AntharasManager;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author pchayka
 */

public class AntharasMinion extends Fighter {
    public AntharasMinion(NpcInstance actor) {
        super(actor);
        actor.startDebuffImmunity();
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        for (Player p : AntharasManager.getZone().getInsidePlayers()) {
            notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        getActor().doCast(SkillTable.getInstance().getSkillEntry(5097, 1), getActor(), true);
        super.onEvtDead(killer);
    }

    @Override
    protected void returnHome(boolean clearAggro, boolean teleport) {
        return;
    }
}