package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.AttributeItem;

import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author VISTALL
 */
public class ExBaseAttributeCancelResult extends GameServerPacket {
    private final boolean result;
    private final int objectId;
    private final Element element;

    public ExBaseAttributeCancelResult(final boolean result, final ItemInstance item, final Element element) {
        this.result = result;
        objectId = item.getObjectId();
        this.element = element;
    }

    @Override
    protected void writeData() {
        writeD(result);
        writeD(objectId);
        writeD(element.getId());
    }
}