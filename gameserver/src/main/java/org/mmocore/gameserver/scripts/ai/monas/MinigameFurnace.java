package org.mmocore.gameserver.scripts.ai.monas;

import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.model.entity.events.impl.MonasteryOfSilenceMiniGameEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author VISTALL
 * @date 14:37/04.05.2012
 */
public class MinigameFurnace extends CharacterAI {
    public MinigameFurnace(Creature actor) {
        super(actor);
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        super.onEvtSeeSpell(skill, caster);

        if (skill.getId() == 9059) {
            final NpcInstance actor = (NpcInstance) getActor();

            final MonasteryOfSilenceMiniGameEvent event = getActor().getEvent(MonasteryOfSilenceMiniGameEvent.class);
            if (!event.isInProgress() || event.getPlayer() != caster) {
                return;
            }

            event.fireFurnace(actor);

            caster.setTarget(null);
        }
    }
}
