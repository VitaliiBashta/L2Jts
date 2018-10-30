package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class ChestInstance extends MonsterInstance {
    public ChestInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    public void tryOpen(final Player opener, final Skill skill) {
        getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, opener, 100);
    }

    @Override
    public boolean isChest() {
        return true;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}