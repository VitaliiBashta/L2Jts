package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author : Mangol
 */
public class Effect_i_bonuscount_up extends Effect {
    private final int count;

    public Effect_i_bonuscount_up(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        count = template.getParam().getInteger("bonuscount", 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = (Player) getEffector();
        player.getRecommendationComponent().addRecomHave(count);
        player.sendPacket(new SystemMessage(SystemMsg.YOU_OBTAINED_S1_RECOMMENDS).addNumber(count));
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
