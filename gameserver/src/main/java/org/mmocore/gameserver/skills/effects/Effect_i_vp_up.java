package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author Mangol
 */
public class Effect_i_vp_up extends Effect {
    private final int _arg;

    public Effect_i_vp_up(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _arg = template.getParam().getInteger("argument");
    }

    @Override
    public boolean checkCondition() {
        if (!getEffected().isPlayer()) {
            return false;
        }
        if (getEffected().getPlayer().getNevitComponent().isBlessingActive()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        final Player player = (Player) getEffected();
        player.getVitalityComponent().addVitality(_arg);
        player.sendPacket(SystemMsg.YOU_HAVE_GAINED_VITALITY_POINTS);
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
