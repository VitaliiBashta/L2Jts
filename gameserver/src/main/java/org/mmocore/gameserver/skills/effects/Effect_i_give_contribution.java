package org.mmocore.gameserver.skills.effects;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.instances.ChestInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author Mangol
 */
public class Effect_i_give_contribution extends Effect {
    private final int _arg;

    public Effect_i_give_contribution(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _arg = template.getParam().getInteger("argument");
    }

    @Override
    public void onStart() {
        final Player player = (Player) getEffector();
        final ChestInstance chest = (ChestInstance) getEffected();
        final long chance = Math.abs(100 - _arg);
        if (Rnd.get(chance) < 100) {
            chest.tryOpen(player, getSkill().getTemplate());
        } else {
            chest.broadcastPacket(new PlaySound(PlaySound.Type.SOUND, "ItemSound2.broken_key", 1, player.getObjectId(), chest.getLoc()));
            chest.suicide(player);
        }
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
