package org.mmocore.gameserver.phantoms.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.clientCustoms.PhantomConfig;
import org.mmocore.gameserver.phantoms.action.MoveToNpcAction;
import org.mmocore.gameserver.phantoms.action.RandomMoveAction;
import org.mmocore.gameserver.phantoms.action.RandomUserAction;
import org.mmocore.gameserver.phantoms.model.Phantom;

/**
 * Created by Hack
 * Date: 23.08.2016 6:32
 */
public class PhantomTownAi extends AbstractPhantomAi {
    public PhantomTownAi(Phantom actor) {
        super(actor);
    }

    @Override
    public void runImpl() {
        if (Rnd.chance(PhantomConfig.randomMoveChance))
            actor.doAction(new RandomMoveAction());
        else if (Rnd.chance(PhantomConfig.moveToNpcChance))
            actor.doAction(new MoveToNpcAction());
        else if (Rnd.chance(PhantomConfig.userActionChance))
            actor.doAction(new RandomUserAction());
    }

    @Override
    public PhantomAiType getType() {
        return PhantomAiType.TOWN;
    }
}
