package org.mmocore.gameserver.scripts.ai.events;

import org.mmocore.commons.math.random.RndSelector;
import org.mmocore.commons.math.random.RndSelector.RndNode;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.tables.SkillTable;

public class SpecialTree extends DefaultAI {
    private static final RndSelector<Integer> SOUNDS = RndSelector.of(RndNode.of(2140, 20), RndNode.of(2142, 20),
            RndNode.of(2145, 20), RndNode.of(2147, 20), RndNode.of(2149, 20));

    private boolean _buffsEnabled = false;
    private int _timer = 0;

    public SpecialTree(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        if (_buffsEnabled) {
            _timer++;
            if (_timer >= 180) {
                _timer = 0;

                final NpcInstance actor = getActor();
                if (actor == null) {
                    return false;
                }

                addTaskBuff(actor, SkillTable.getInstance().getSkillEntry(2139, 1));

                if (Rnd.chance(33)) {
                    actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, SOUNDS.select(), 1, 500, 0));
                }
            }
        }

        return super.thinkActive();
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        _buffsEnabled = !getActor().isInZonePeace();
        _timer = 0;
    }
}
