package org.mmocore.gameserver.scripts.npc.model.residences.castle;

import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.CastleSiegeInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class CastleMessengerInstance extends NpcInstance {
    private final String fnMyLord = "residence2/castle/sir_tyron007.htm";
    private final String fnSiegeMyLord = "residence2/castle/sir_tyron021.htm";
    private final String fnSiegeAnother = "residence2/castle/sir_tyron022.htm";

    public CastleMessengerInstance(int objectID, NpcTemplate template) {
        super(objectID, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        Castle castle = getCastle();
        if (player.isCastleLord(castle.getId())) {
            if (castle.getSiegeEvent().isInProgress()) {
                showChatWindow(player, fnSiegeMyLord);
            } else {
                showChatWindow(player, fnMyLord);
            }
        } else if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress()) {
            showChatWindow(player, fnSiegeAnother);
        } else {
            player.sendPacket(new CastleSiegeInfo(castle, player));
        }
    }
}