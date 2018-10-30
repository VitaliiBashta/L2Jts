package org.mmocore.gameserver.skills.effects;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_AgathionEnergyInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author : Mangol
 * @date : 24.04.14  23:31
 */
public class Effect_i_agathion_energy extends Effect {
    private int _power;

    public Effect_i_agathion_energy(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _power = template.getParam().getInteger("argument", 0);
    }

    @Override
    public void onStart() {
        final Player player = (Player) getEffected();
        final ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
        if (item != null) {
            item.setAgathionEnergy(item.getAgathionEnergy() + _power);
            item.setJdbcState(JdbcEntityState.UPDATED);
            item.update();
            player.sendPacket(new ExBR_AgathionEnergyInfo(1, item));
        }
        if (_power > 0) {
            player.sendPacket(new SystemMessage(SystemMsg.ENERGY_S1_REPLENISHED).addNumber(_power));
        }
    }

    @Override
    public void onExit() {
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}