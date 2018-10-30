package org.mmocore.gameserver.scripts.ai.isle_of_prayer;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.instances.CrystalCaverns;
import org.mmocore.gameserver.skills.SkillEntry;

public class EvasProtector extends DefaultAI {
    public EvasProtector(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        NpcInstance actor = getActor();
        if (skill.getSkillType() == Skill.SkillType.HEAL && actor.getReflection().getInstancedZoneId() == 10) {
            ((CrystalCaverns) actor.getReflection()).notifyProtectorHealed(actor);
        }
        super.onEvtSeeSpell(skill, caster);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}