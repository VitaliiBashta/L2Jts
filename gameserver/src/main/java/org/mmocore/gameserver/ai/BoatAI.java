package org.mmocore.gameserver.ai;

import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Creature;

/**
 * Author: VISTALL
 * Date:  16:56/28.12.2010
 */
public class BoatAI extends CharacterAI {
    public BoatAI(final Creature actor) {
        super(actor);
    }

    @Override
    protected void onEvtArrived() {
        final Boat actor = (Boat) getActor();
        if (actor == null) {
            return;
        }

        actor.onEvtArrived();
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}
