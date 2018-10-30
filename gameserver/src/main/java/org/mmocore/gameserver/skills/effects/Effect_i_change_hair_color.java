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
public class Effect_i_change_hair_color extends Effect {
    private final int type;

    public Effect_i_change_hair_color(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        type = template.getParam().getInteger("argument");
    }

    @Override
    public void onStart() {
        Player player = (Player) getEffector();
        player.getAppearanceComponent().setHairColor(type);
        player.sendPacket(new SystemMessage(SystemMsg.YOUR_HAIR_COLOR_HAS_BEEN_CHANGED));
        player.broadcastCharInfo();
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}