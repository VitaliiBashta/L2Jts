package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.manager.CoupleManager;
import org.mmocore.gameserver.model.entity.Couple;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class WeddingManagerInstance extends NpcInstance {
    public WeddingManagerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    private static boolean isWearingFormalWear(final Player player) {
        if (player != null && player.getInventory() != null && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST) == ItemTemplate.ITEM_ID_FORMAL_WEAR) {
            return true;
        }
        return false;
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        final String filename = "wedding/start.htm";
        final String replace = "";
        final HtmlMessage html = new HtmlMessage(this);
        html.setFile(filename);
        html.replace("%replace%", replace);
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        // standard msg
        String filename = "wedding/start.htm";
        String replace = "";

        // if player has no partner
        if (player.getPartnerId() == 0) {
            filename = "wedding/nopartner.htm";
            sendHtmlMessage(player, filename, replace);
            return;
        }

        final Player ptarget = GameObjectsStorage.getPlayer(player.getPartnerId());

        // partner online ?
        if (ptarget == null || !ptarget.isOnline()) {
            filename = "wedding/notfound.htm";
            sendHtmlMessage(player, filename, replace);
            return;
        } else if (player.isMaried()) // already married ?
        {
            filename = "wedding/already.htm";
            sendHtmlMessage(player, filename, replace);
            return;
        } else if (command.startsWith("AcceptWedding")) {
            // accept the wedding request
            player.setMaryAccepted(true);
            final Couple couple = CoupleManager.getInstance().getCouple(player.getCoupleId());
            couple.marry();

            //messages to the couple
            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2WeddingManagerMessage"));
            player.setMaried(true);
            player.setMaryRequest(false);
            ptarget.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2WeddingManagerMessage"));
            ptarget.setMaried(true);
            ptarget.setMaryRequest(false);

            //wedding march
            player.broadcastPacket(new MagicSkillUse(player, player, 2230, 1, 1, 0));
            ptarget.broadcastPacket(new MagicSkillUse(ptarget, ptarget, 2230, 1, 1, 0));

            // fireworks
            player.broadcastPacket(new MagicSkillUse(player, player, 2025, 1, 1, 0));
            ptarget.broadcastPacket(new MagicSkillUse(ptarget, ptarget, 2025, 1, 1, 0));

            AnnouncementUtils.announceToAll(new CustomMessage("wedding.announce").addString(player.getName()).addString(ptarget.getName()).toString(Language.ENGLISH));

            filename = "wedding/accepted.htm";
            replace = ptarget.getName();
            sendHtmlMessage(ptarget, filename, replace);
            return;
        } else if (player.isMaryRequest()) {
            // check for formalwear
            if (ServerConfig.WEDDING_FORMALWEAR && !isWearingFormalWear(player)) {
                filename = "wedding/noformal.htm";
                sendHtmlMessage(player, filename, replace);
                return;
            }
            filename = "wedding/ask.htm";
            player.setMaryRequest(false);
            ptarget.setMaryRequest(false);
            replace = ptarget.getName();
            sendHtmlMessage(player, filename, replace);
            return;
        } else if (command.startsWith("AskWedding")) {
            // check for formalwear
            if (ServerConfig.WEDDING_FORMALWEAR && !isWearingFormalWear(player)) {
                filename = "wedding/noformal.htm";
                sendHtmlMessage(player, filename, replace);
                return;
            } else if (player.getAdena() < ServerConfig.WEDDING_PRICE) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            } else {
                player.setMaryAccepted(true);
                ptarget.setMaryRequest(true);
                replace = ptarget.getName();
                filename = "wedding/requested.htm";
                player.reduceAdena(ServerConfig.WEDDING_PRICE, true);
                sendHtmlMessage(player, filename, replace);
                return;
            }
        } else if (command.startsWith("DeclineWedding")) {
            player.setMaryRequest(false);
            ptarget.setMaryRequest(false);
            player.setMaryAccepted(false);
            ptarget.setMaryAccepted(false);
            player.sendMessage("You declined");
            ptarget.sendMessage("Your partner declined");
            replace = ptarget.getName();
            filename = "wedding/declined.htm";
            sendHtmlMessage(ptarget, filename, replace);
            return;
        } else if (player.isMaryAccepted()) {
            filename = "wedding/waitforpartner.htm";
            sendHtmlMessage(player, filename, replace);
            return;
        }
        sendHtmlMessage(player, filename, replace);
    }

    private void sendHtmlMessage(final Player player, final String filename, final String replace) {
        final HtmlMessage html = new HtmlMessage(this);
        html.setFile(filename);
        html.replace("%replace%", replace);
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }
}
