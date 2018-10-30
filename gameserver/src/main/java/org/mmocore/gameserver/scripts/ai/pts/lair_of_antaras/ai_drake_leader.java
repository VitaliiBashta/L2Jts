package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_drake_leader extends Fighter {
    private final String Privates = "22849:1:0";
    private final String Privates1 = "22850:1:0";
    private final String Privates2 = "22851:1:0";
    private final String Privates3 = "22849:2:0";

    public ai_drake_leader(final NpcInstance actor) {
        super(actor);
        AI_TASK_ACTIVE_DELAY = 250;
        AI_TASK_ATTACK_DELAY = 1000;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final NpcInstance actor = getActor();
        if (actor.hasPrivates()) {
            actor.getPrivatesList().useSpawnPrivates();
        } else if (!actor.hasPrivates()) {
            if (getSuperPoint() != null) {
                actor.getPrivatesList().createPrivates(Privates3, true);
                for (int i0 = 0; i0 < 2; i0++) {
                    final int i1 = Rnd.get(1, 3);
                    switch (i1) {
                        case 1:
                            actor.getPrivatesList().createPrivates(Privates, true);
                            break;
                        case 2:
                            actor.getPrivatesList().createPrivates(Privates1, true);
                            break;
                        case 3:
                            actor.getPrivatesList().createPrivates(Privates2, true);
                            break;
                    }
                }
            } else {
                for (int i0 = 0; i0 < 4; i0++) {
                    final int i1 = Rnd.get(1, 3);
                    switch (i1) {
                        case 1:
                            actor.getPrivatesList().createPrivates(Privates, true);
                            break;
                        case 2:
                            actor.getPrivatesList().createPrivates(Privates1, true);
                            break;
                        case 3:
                            actor.getPrivatesList().createPrivates(Privates2, true);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public boolean checkAggression(final Creature target) {
        final NpcInstance actor = getActor();
        if (target.isPlayable() && !target.isDead() && !target.isInvisible()) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            if (actor.hasPrivates()) {
                for (final NpcInstance minion : actor.getPrivatesList().getAlivePrivates()) {
                    minion.getAggroList().addDamageHate(target, 0, 1);
                    minion.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean maybeMoveToHome() {
        return false;
    }

    @Override
    protected void teleportHome() {
    }
}