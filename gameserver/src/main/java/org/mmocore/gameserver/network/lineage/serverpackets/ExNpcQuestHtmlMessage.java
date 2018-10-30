package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;


/**
 * @author VISTALL
 * @date 16:25/24.04.2011
 */
public class ExNpcQuestHtmlMessage extends GameServerPacket {
    private final int npcObjId;
    private final CharSequence html;
    private final int questId;

    public ExNpcQuestHtmlMessage(final int npcObjId, final CharSequence html, final int questId) {
        this.npcObjId = npcObjId;
        this.html = html;
        this.questId = questId;
    }

    @Override
    protected void writeData() {
        writeD(npcObjId);
        writeS(html);
        writeD(questId);
    }
}
