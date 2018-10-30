package org.mmocore.gameserver.object.components.player.custom.acp.action;

import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.custom.acp.AcpComponent;
import org.mmocore.gameserver.object.components.player.custom.acp.action.abstracts.AbstractAction;
import org.mmocore.gameserver.utils.Util;

/**
 * Create by Mangol on 30.12.2015.
 */
public class CpAction extends AbstractAction {
    public CpAction(final AcpComponent component) {
        super(component);
    }

    @Override
    public void useAction() {
        if (!isCondition(getPlayer())) {
            return;
        }
        final ItemInstance itemInstance = getPlayer().getInventory().getItemByItemId(getTemplate().getCpItemId());
        if (itemInstance != null) {
            if (getPlayer().getCurrentCp() == getPlayer().getMaxCp()) {
                getPlayer().sendMessage(new CustomMessage("component.acp.maxValue").addString("CP").toString(getPlayer()));
                getComponent().stopAutoCp();
                return;
            }
            Util.useItem(getPlayer(), itemInstance.getObjectId(), false);
            if (getPlayer().getCurrentCp() == getPlayer().getMaxCp()) {
                getPlayer().sendMessage(new CustomMessage("component.acp.maxValue").addString("CP").toString(getPlayer()));
                getComponent().stopAutoCp();
            }
        } else {
            getPlayer().sendMessage(new CustomMessage("component.acp.noCorrectItem").addString("CP").
                    addString(Util.getItemName(getPlayer().getLanguage(), getTemplate().getCpItemId())).
                    toString(getPlayer()));
            getComponent().stopAutoCp();
        }
    }
}
