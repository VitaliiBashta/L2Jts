package org.mmocore.gameserver.scripts.npc.model.residences.castle;

import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 21:58/23.05.2011
 * 35506
 */
public class VenomTeleporterInstance extends NpcInstance {
    private static final long serialVersionUID = -1716591883554340489L;

    public VenomTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        Castle castle = getCastle();
        if (castle.getSiegeEvent().isInProgress()) {
            showChatWindow(player, "residence2/castle/rune_massymore_teleporter002.htm");
        } else if (!checkForDominionWard(player)) {
            player.teleToLocation(12589, -49044, -3008);
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "residence2/castle/rune_massymore_teleporter001.htm");
    }
}
