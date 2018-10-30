package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.commons.net.nio.impl.SendablePacket;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.base.MultiSellIngredient;
import org.mmocore.gameserver.model.buylist.Product;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2GameServerPacket extends SendablePacket<GameClient> implements IBroadcastPacket {
    private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);

    @Override
    public final boolean write() {
        try {
            writeImpl();
            return true;
        } catch (Exception e) {
            _log.error("Client: " + getClient() + " - Failed writing: " + getType() + " - Server Version: " +
                    GameServer.getInstance().getVersion().getRevisionNumber(), e);
        }
        return false;
    }

    protected abstract void writeImpl();

    protected void writeEx(final int value) {
        writeC(0xFE);
        writeH(value);
    }

    protected void writeD(final boolean b) {
        writeD(b ? 1 : 0);
    }

    /**
     * Отсылает число позиций + массив
     */
    protected void writeDD(final int[] values, final boolean sendCount) {
        if (sendCount) {
            getByteBuffer().putInt(values.length);
        }
        for (final int value : values) {
            getByteBuffer().putInt(value);
        }
    }

    protected void writeDD(final int[] values) {
        writeDD(values, false);
    }

    protected void writeItemInfo(final ItemInstance item) {
        writeItemInfo(item, item.getCount());
    }

    protected void writeItemInfo(final ItemInstance item, final long count) {
        writeD(item.getObjectId());
        writeD(item.getItemId());
        writeD(item.getEquipSlot());
        writeQ(count);
        writeH(item.getTemplate().getType2ForPackets());
        writeH(item.getCustomType1());
        writeH(item.isEquipped() ? 1 : 0);
        writeD(item.getBodyPart());
        writeH(item.getEnchantLevel());
        writeH(item.getCustomType2());
        writeH(item.getVariation1Id());
        writeH(item.getVariation2Id());
        writeD(item.getShadowLifeTime());
        writeD(item.getTemporalLifeTime());
        writeH(item.getAttackElement().getId());
        writeH(item.getAttackElementValue());
        writeH(item.getDefenceFire());
        writeH(item.getDefenceWater());
        writeH(item.getDefenceWind());
        writeH(item.getDefenceEarth());
        writeH(item.getDefenceHoly());
        writeH(item.getDefenceUnholy());
        writeH(item.getEnchantOptions()[0]);
        writeH(item.getEnchantOptions()[1]);
        writeH(item.getEnchantOptions()[2]);
    }

    protected void writeItemInfo(final ItemInfo item) {
        writeItemInfo(item, item.getCount());
    }

    protected void writeItemInfo(final ItemInfo item, final long count) {
        writeD(item.getObjectId());
        writeD(item.getItemId());
        writeD(item.getEquipSlot());
        writeQ(count);
        writeH(item.getItem().getType2ForPackets());
        writeH(item.getCustomType1());
        writeH(item.isEquipped() ? 1 : 0);
        writeD(item.getItem().getBodyPart());
        writeH(item.getEnchantLevel());
        writeH(item.getCustomType2());
        writeH(item.getVariation1Id());
        writeH(item.getVariation2Id());
        writeD(item.getShadowLifeTime());
        writeD(item.getTemporalLifeTime());
        writeH(item.getAttackElement());
        writeH(item.getAttackElementValue());
        writeH(item.getDefenceFire());
        writeH(item.getDefenceWater());
        writeH(item.getDefenceWind());
        writeH(item.getDefenceEarth());
        writeH(item.getDefenceHoly());
        writeH(item.getDefenceUnholy());
        writeH(item.getEnchantOptions()[0]);
        writeH(item.getEnchantOptions()[1]);
        writeH(item.getEnchantOptions()[2]);
    }

    protected void writeItemElements(final MultiSellIngredient item) {
        if (item.getItemId() <= 0) {
            writeItemElements();
            return;
        }
        final ItemTemplate i = ItemTemplateHolder.getInstance().getTemplate(item.getItemId());
        if (item.getItemAttributes().getValue() > 0) {
            if (i.isWeapon()) {
                final Element e = item.getItemAttributes().getElement();
                writeH(e.getId()); // attack element (-1 - none)
                writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e)); // attack element value
                writeH(0); // водная стихия (fire pdef)
                writeH(0); // огненная стихия (water pdef)
                writeH(0); // земляная стихия (wind pdef)
                writeH(0); // воздушная стихия (earth pdef)
                writeH(0); // темная стихия (holy pdef)
                writeH(0); // светлая стихия (dark pdef)
            } else if (i.isArmor()) {
                writeH(-1); // attack element (-1 - none)
                writeH(0); // attack element value
                for (final Element e : Element.VALUES) {
                    writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e));
                }
            } else {
                writeItemElements();
            }
        } else {
            writeItemElements();
        }
    }

    protected void writeItemElements() {
        writeH(-1); // attack element (-1 - none)
        writeH(0x00); // attack element value
        writeH(0x00); // водная стихия (fire pdef)
        writeH(0x00); // огненная стихия (water pdef)
        writeH(0x00); // земляная стихия (wind pdef)
        writeH(0x00); // воздушная стихия (earth pdef)
        writeH(0x00); // темная стихия (holy pdef)
        writeH(0x00); // светлая стихия (dark pdef)
    }

    protected void writeItemInfo(final Product item) {
        writeD(item.getItemId());
        writeD(item.getItemId());
        writeD(0x00);
        writeQ(item.getCount());
        writeH(item.getItem().getType2());
        writeH(item.getItem().getType1());
        writeH(0x00);
        writeD(item.getItem().getBodyPart());
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeD(-1);
        writeD(-9999);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
    }

    public String getType() {
        return "[S] " + getClass().getSimpleName();
    }

    public L2GameServerPacket packet(final Player player) {
        return this;
    }

}