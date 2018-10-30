package org.mmocore.gameserver.scripts.npc.model.residences.castle;

import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.CastleSiegeInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class CastleMessengerInstance extends NpcInstance {

    public CastleMessengerInstance(int objectID, NpcTemplate template) {
        super(objectID, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        Castle castle = getCastle();
        if (player.isCastleLord(castle.getId())) {
            if (castle.getSiegeEvent().isInProgress()) {
                String fnSiegeMyLord = "residence2/castle/sir_tyron021.htm";
                showChatWindow(player, fnSiegeMyLord);
            } else {
                String fnMyLord = "residence2/castle/sir_tyron007.htm";
                showChatWindow(player, fnMyLord);
            }
        } else if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress()) {
            String fnSiegeAnother = "residence2/castle/sir_tyron022.htm";
            showChatWindow(player, fnSiegeAnother);
        } else {
            player.sendPacket(new CastleSiegeInfo(castle, player));
        }
    }
}