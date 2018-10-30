package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author Mangol
 * @version PTS Freya
 * @npc_id 18820 18823 18838
 */

public class dream_box extends DefaultAI {
    private int i_ai8 = 0;
    private int Rate = 25;

    public dream_box(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        Player player = attacker.getPlayer();
        int n1 = Rnd.get(100);
        if (actor.getCurrentHpPercents() <= 10) {
            if (n1 <= Rate) {
                if (i_ai8 == 0) {
                    if (n1 <= 33) {
                        actor.dropItem(player, 4042, 3);
                    }
                    if (n1 <= 50) {
                        actor.dropItem(player, 4043, 4);
                    }
                    if (n1 <= 50) {
                        actor.dropItem(player, 4044, 4);
                    }
                    if (n1 <= 16) {
                        actor.dropItem(player, 9628, 2);
                    }
                    actor.doCast(SkillTable.getInstance().getSkillEntry(5758, 1), actor, true);
                    i_ai8 = 1;
                }
            } else
                for (NpcInstance npc : actor.getReflection().getNpcs()) {
                    if (npc.getNpcId() == actor.getNpcId()) {
                        actor.doCast(SkillTable.getInstance().getSkillEntry(5376, 4), actor, true);
                        npc.deleteMe();
                        i_ai8 = 1;
                    }
                }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}
