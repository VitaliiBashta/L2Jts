package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.cache.ItemInfoCache;
import org.mmocore.gameserver.data.client.holder.ItemNameLineHolder;
import org.mmocore.gameserver.listener.actor.player.impl.PlayerChatListener;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SysString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.templates.client.ItemNameLine;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.Log;

import java.util.regex.Matcher;

public class Say2 extends NpcStringContainer {
    private final ChatType type;
    private final int objectId;
    private Language itemLinkLang;
    private SysString sysString;
    private SystemMsg systemMsg;
    private String charName;

    public Say2(final int objectId, final ChatType type, final SysString st, final SystemMsg sm) {
        super(NpcString.NONE);
        this.objectId = objectId;
        this.type = type;
        sysString = st;
        systemMsg = sm;
    }

    public Say2(final int objectId, final ChatType type, final String charName, final String text, final Language itemLinkLang) {
        this(objectId, type, charName, NpcString.NONE, text);

        this.itemLinkLang = itemLinkLang;
    }

    public Say2(final int objectId, final ChatType type, final String charName, final NpcString npcString, final String... params) {
        super(npcString, params);
        this.objectId = objectId;
        this.type = type;
        this.charName = charName;
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeD(type.ordinal());
        switch (type) {
            case SYSTEM_MESSAGE:
                writeD(sysString.getId());
                writeD(systemMsg.getId());
                break;
            default:
                writeS(charName);
                writeElements();
                break;
        }
    }

    @Override
    public L2GameServerPacket packet(final Player player) {
        final Language lang = player.getLanguage();
        // если _itemLinkLang нулл(тоисть нету итем линков), или язык совпадает с языком игрока - возращаем this
        if (itemLinkLang == null || itemLinkLang == lang) {
            return this;
        }

        String text = null;
        final Matcher m = PlayerChatListener.EX_ITEM_LINK_PATTERN.matcher(parameters[0]);
        while (m.find()) {
            final int objectId = Integer.parseInt(m.group(1));
            final ItemInfo itemInfo = ItemInfoCache.getInstance().get(objectId);

            if (itemInfo != null) {
                final ItemNameLine line = ItemNameLineHolder.getInstance().get(lang, itemInfo.getItemId());
                if (line != null) {
                    String replace = line.getName();
                    if (itemInfo.getVariation1Id() > 0 || itemInfo.getVariation2Id() > 0) {
                        replace = line.getAugmentName();
                    }

                    text = (text == null ? parameters[0] : text).replace(m.group(2), replace);
                }
            } else {
                Log.audit("[Say2]", "Player(ID:" + player.getObjectId() + ") name: " + player.getName() + " trying send not correct item: " + objectId);
            }
        }

        return new Say2(objectId, type, charName, npcString, text);
    }
}