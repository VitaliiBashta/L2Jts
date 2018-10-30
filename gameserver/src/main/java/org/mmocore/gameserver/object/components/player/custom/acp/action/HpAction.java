package org.mmocore.gameserver.object.components.player.custom.acp.action;

import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.custom.acp.AcpComponent;
import org.mmocore.gameserver.object.components.player.custom.acp.action.abstracts.AbstractAction;
import org.mmocore.gameserver.utils.Util;

/**
 * Create by Mangol on 30.12.2015.
 */
public class HpAction extends AbstractAction {
    public HpAction(final AcpComponent component) {
        super(component);
    }

    @Override
    public void useAction() {
        if (!isCondition(getPlayer())) {
            return;
        }
        final ItemInstance itemInstance = getPlayer().getInventory().getItemByItemId(getTemplate().getHpItemId());
        if (itemInstance != null) {
            if (getPlayer().getCurrentHp() == getPlayer().getMaxHp()) {
                getPlayer().sendMessage(new CustomMessage("component.acp.maxValue").addString("HP").toString(getPlayer()));
                getComponent().stopAutoHp();
                return;
            }
            Util.useItem(getPlayer(), itemInstance.getObjectId(), false);
            if (getPlayer().getCurrentHp() == getPlayer().getMaxHp()) {
                getPlayer().sendMessage(new CustomMessage("component.acp.maxValue").addString("HP").toString(getPlayer()));
                getComponent().stopAutoHp();
            }
        } else {
            getPlayer().sendMessage(new CustomMessage("component.acp.noCorrectItem").addString("HP").
                    addString(Util.getItemName(getPlayer().getLanguage(), getTemplate().getHpItemId())).
                    toString(getPlayer()));
            getComponent().stopAutoHp();
        }
    }
}
