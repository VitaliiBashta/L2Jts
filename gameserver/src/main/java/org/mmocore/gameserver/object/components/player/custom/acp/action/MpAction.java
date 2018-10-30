package org.mmocore.gameserver.object.components.player.custom.acp.action;

import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.custom.acp.AcpComponent;
import org.mmocore.gameserver.object.components.player.custom.acp.action.abstracts.AbstractAction;
import org.mmocore.gameserver.utils.Util;

/**
 * Create by Mangol on 30.12.2015.
 */
public class MpAction extends AbstractAction {
    public MpAction(final AcpComponent component) {
        super(component);
    }

    @Override
    public void useAction() {
        if (!isCondition(getPlayer())) {
            return;
        }
        final ItemInstance itemInstance = getPlayer().getInventory().getItemByItemId(getTemplate().getMpItemId());
        if (itemInstance != null) {
            if (getPlayer().getCurrentMp() == getPlayer().getMaxMp()) {
                getPlayer().sendMessage(new CustomMessage("component.acp.maxValue").addString("MP").toString(getPlayer()));
                getComponent().stopAutoMp();
                return;
            }
            Util.useItem(getPlayer(), itemInstance.getObjectId(), false);
            if (getPlayer().getCurrentMp() == getPlayer().getMaxMp()) {
                getPlayer().sendMessage(new CustomMessage("component.acp.maxValue").addString("MP").toString(getPlayer()));
                getComponent().stopAutoMp();
            }
        } else {
            getPlayer().sendMessage(new CustomMessage("component.acp.noCorrectItem").addString("MP").
                    addString(Util.getItemName(getPlayer().getLanguage(), getTemplate().getMpItemId())).
                    toString(getPlayer()));
            getComponent().stopAutoMp();
        }
    }
}
