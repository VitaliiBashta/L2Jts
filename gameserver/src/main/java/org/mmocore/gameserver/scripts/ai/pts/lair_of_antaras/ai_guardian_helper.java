package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_guardian_helper extends DefaultAI {
    public ai_guardian_helper(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance actor = getActor();
        actor.setTargetable(false);
        actor.setIsInvul(true);
        final int i0 = Rnd.get(1, 2);
        if (i0 == 1) {
            actor.getAroundCharacters(900, 300).stream().filter(target -> target.isMonster() && target.getNpcId() == 22857 && !target.isDead()).forEach(target -> {
                actor.altOnMagicUseTimer(target, SkillTable.getInstance().getSkillEntry(6745, 1));
            });
        } else if (i0 == 2) {
            actor.getAroundCharacters(900, 300).stream().filter(target -> target.isPlayer() && !target.isDead()).forEach(target -> actor.altOnMagicUseTimer(target, SkillTable.getInstance().getSkillEntry(6746, 1)));
        }
        super.onEvtSpawn();
    }
}