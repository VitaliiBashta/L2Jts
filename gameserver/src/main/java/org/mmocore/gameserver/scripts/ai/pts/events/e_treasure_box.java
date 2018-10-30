package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class e_treasure_box extends Fighter {
    private static final int s_item_box_key_event1 = 2322;

    public e_treasure_box(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        getActor().setIsInvul(true);
    }

    @Override
    protected void onEvtSeeSpell(final SkillEntry skill, final Creature caster) {
        if (skill != null && caster != null) {
            if (caster.getCastingTarget() != null && caster.getCastingTarget() == getActor() && skill.getId() == s_item_box_key_event1) {
                getActor().setIsInvul(false);
            }
        }
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}