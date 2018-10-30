package org.mmocore.gameserver.skills.effects;

import org.jts.dataparser.data.holder.cubicdata.Agathion;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_AgathionEnergyInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author Mangol
 * @since 02.01.2017
 */
public class Effect_c_agathion_energy extends Effect {
    private final int power;
    private final int positivePower;

    public Effect_c_agathion_energy(Creature creature, Creature target, SkillEntry skill, EffectTemplate template) {
        super(creature, target, skill, template);
        String[] args = template.getParam().getString("argument").split(";");
        power = Integer.parseInt(args[0]);
        positivePower = Math.abs(power);
        int period = Integer.parseInt(args[1]);
        setPeriod(period * 1000);
    }

    @Override
    protected boolean onActionTime() {
        if (getEffected().isDead()) {
            return false;
        }

        if (!getEffected().isPlayer())
            return false;

        if (getEffected().getPlayer().getAgathion() != null) {
            final Agathion agathion = getEffected().getPlayer().getAgathion().getTemplate();
            final ItemInstance item = getEffected().getPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
            if (item != null) {
                if (positivePower > item.getAgathionEnergy()) {
                    getEffected().getPlayer().sendPacket(new SystemMessage(SystemMsg.THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY));
                    getEffected().getPlayer().sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
                    return false;
                } else if (agathion.item_ids.length > 0) {
                    if (item.getItemId() != agathion.item_ids[0])
                        return false;
                    item.setAgathionEnergy(item.getAgathionEnergy() + power);
                    getEffected().sendPacket(new ExBR_AgathionEnergyInfo(1, item));
                } else
                    return false;
                return true;
            }
        }
        return false;
    }
}
