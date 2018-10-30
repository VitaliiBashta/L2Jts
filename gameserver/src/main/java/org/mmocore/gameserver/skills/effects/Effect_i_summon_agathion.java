package org.mmocore.gameserver.skills.effects;

import org.jts.dataparser.data.holder.CubicDataHolder;
import org.jts.dataparser.data.holder.cubicdata.Agathion;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * Create by Mangol on 24.09.2015.
 */
public class Effect_i_summon_agathion extends Effect {
    private final int _id;
    private final int _level;

    public Effect_i_summon_agathion(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        String[] _st = template.getParam().getString("argument").split(";");
        _id = Integer.parseInt(_st[0]);
        _level = Integer.parseInt(_st[1]);
    }

    @Override
    public boolean checkCondition() {
        final Player player = (Player) getEffector();
        if (player.getAgathion() != null) {
            player.sendPacket(SystemMsg.AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED);
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        final Agathion agathion = CubicDataHolder.getInstance().getAgathionTemplate(_id, _level);
        if (agathion != null) {
            final Player player = (Player) getEffected();
            if (player.getAgathion() != null) {
                if (player.getAgathion().getSlot() == agathion.slot) {
                    player.deleteAgathion();
                }
            }
            player.addAgathion(agathion);
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }

    @Override
    public void onExit() {
    }
}
