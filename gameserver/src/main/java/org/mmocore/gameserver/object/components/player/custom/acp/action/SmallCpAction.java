package org.mmocore.gameserver.object.components.player.custom.acp.action;

import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.custom.acp.AcpComponent;
import org.mmocore.gameserver.object.components.player.custom.acp.action.abstracts.AbstractAction;
import org.mmocore.gameserver.utils.Util;

/**
 * @author Mangol
 * @since 29.09.2016
 */
public class SmallCpAction extends AbstractAction {
    public SmallCpAction(AcpComponent component) {
        super(component);
    }

    @Override
    public void useAction() {
        if (!isCondition(getPlayer())) {
            return;
        }
        final ItemInstance itemInstance = getPlayer().getInventory().getItemByItemId(getTemplate().getSmallCpItemId());
        if (itemInstance != null) {
            if (getPlayer().getCurrentCp() == getPlayer().getMaxCp()) {
                getPlayer().sendMessage(new CustomMessage("component.acp.maxValue").addString("Small CP").toString(getPlayer()));
                getComponent().stopAutoSmallCp();
                return;
            }
            Util.useItem(getPlayer(), itemInstance.getObjectId(), false);
            if (getPlayer().getCurrentCp() == getPlayer().getMaxCp()) {
                getPlayer().sendMessage(new CustomMessage("component.acp.maxValue").addString("Small CP").toString(getPlayer()));
                getComponent().stopAutoSmallCp();
            }
        } else {
            getPlayer().sendMessage(new CustomMessage("component.acp.noCorrectItem").addString("Small CP").
                    addString(Util.getItemName(getPlayer().getLanguage(), getTemplate().getSmallCpItemId())).
                    toString(getPlayer()));
            getComponent().stopAutoSmallCp();
        }
    }
}
