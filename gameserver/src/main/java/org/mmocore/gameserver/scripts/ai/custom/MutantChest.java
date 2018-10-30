package org.mmocore.gameserver.scripts.ai.custom;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;

public class MutantChest extends Fighter {

    public MutantChest(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (Rnd.chance(30)) {
            Functions.npcSay(actor, "Враги! Всюду враги! Все сюда, враги здесь!");
        }

        actor.deleteMe();
    }
}