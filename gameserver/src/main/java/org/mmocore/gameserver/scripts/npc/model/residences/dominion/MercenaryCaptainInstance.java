package org.mmocore.gameserver.scripts.npc.model.residences.dominion;

import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowDominionRegistry;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MercenaryCaptainInstance extends NpcInstance {
    private static final long serialVersionUID = -6505054837853423973L;

    private static final Logger _log = LoggerFactory.getLogger(MercenaryCaptainInstance.class);

    public MercenaryCaptainInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        Dominion dominion = getDominion();
        int badgeId = 13676 + dominion.getId();

        if (command.equalsIgnoreCase("territory_register")) {
            player.sendPacket(new ExShowDominionRegistry(player, dominion));
        } else if (command.startsWith("openMultisell")) {
            int multisell = getNpcId() * 10000 + 1;
            MultiSellHolder.getInstance().SeparateAndSend(multisell, player, getObjectId(), getCastle().getTaxRate());
        } else if (command.startsWith("certificate_multisell")) {
            String params = command.substring(22);
            int itemId = 0;
            int multisell = 0;
            if (Character.isDigit(params.charAt(0))) {
                int id = Integer.parseInt(params); // 2 or 3
                switch (id) {
                    case 2:
                        itemId = 13767;
                        multisell = getNpcId() * 10000 + 2;
                        break;

                    case 3:
                        itemId = 13768;
                        multisell = getNpcId() * 10000 + 3;
                        break;

                    default:
                        _log.info("Error in bypass 'certificate_multisell', actor " + player.getName() + " send id = " + id);
                        break;
                }
            } else
                _log.info("Error in bypass 'certificate_multisell', actor " + player.getName() + " send not valid params: '" + params + '\'');

            if (itemId > 0 && player.getInventory().getCountOf(itemId) > 0)
                MultiSellHolder.getInstance().SeparateAndSend(multisell, player, getObjectId(), getCastle().getTaxRate());
            else
                showChatWindow(player, 25);
        } else if (command.startsWith("BuyTW")) {
            final String params = command.substring(6);
            int itemId = 0;
            int price = 0;

            if (Character.isDigit(params.charAt(0))) {
                int idTW = Integer.parseInt(params);
                switch (idTW) {
                    case 1:
                        itemId = 4422;
                        price = 50;
                        break;

                    case 2:
                        itemId = 4423;
                        price = 50;
                        break;

                    case 3:
                        itemId = 4424;
                        price = 50;
                        break;

                    case 4:
                        itemId = 14819;
                        price = 80;
                        break;

                    default:
                        _log.info("Error in bypass 'BuyTW', actor " + player.getName() + " send idTw = " + idTW);
                        break;
                }
            } else
                _log.info("Error in bypass 'BuyTW', actor " + player.getName() + " send not valid params: '" + params + '\'');

            if (itemId > 0 && ItemFunctions.getItemCount(player, badgeId) >= price) {
                ItemFunctions.removeItem(player, badgeId, price, true);
                ItemFunctions.addItem(player, itemId, 1, true);
                showChatWindow(player, 7);
            } else
                showChatWindow(player, 6);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        if (player.getLevel() < 40 || player.getPlayerClassComponent().getClassId().getLevel().ordinal() <= 2) {
            val = 26;
        } else {
            Castle castle = getCastle();
            Dominion dominion = getDominion();

            if (castle.getOwner() != null && player.getClan() == castle.getOwner() || dominion.getLordObjectId() == player.getObjectId()) {
                if (castle.getSiegeEvent().isInProgress() || dominion.getSiegeEvent().isInProgress()) {
                    val = 21;
                } else {
                    val = 7;
                }
            } else if (castle.getSiegeEvent().isInProgress() || dominion.getSiegeEvent().isInProgress()) {
                val = 22;
            }
        }

        if (val == 0) {
            val = 1;
        }
        return val > 9 ? "residence2/dominion/gludio_merc_captain0" + val + ".htm" : "residence2/dominion/gludio_merc_captain00" + val + ".htm";
    }
}