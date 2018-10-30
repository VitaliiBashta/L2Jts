package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;

/**
 * @author pchayka
 */

public class BelethClone extends Mystic {
    public BelethClone(NpcInstance actor) {
        super(actor);
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