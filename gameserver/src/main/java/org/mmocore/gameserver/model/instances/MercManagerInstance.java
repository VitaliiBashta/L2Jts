package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public final class MercManagerInstance extends MerchantInstance {
    private static final int COND_ALL_FALSE = 0;
    private static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    private static final int COND_OWNER = 2;

    public MercManagerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        int condition = validateCondition(player);
        if (condition <= COND_ALL_FALSE || condition == COND_BUSY_BECAUSE_OF_SIEGE) {
            return;
        }
        if (condition == COND_OWNER) {
            if (command.equalsIgnoreCase("menu_select?ask=-201&reply=1")) {
                showShopWindow(player, 1, false);
            } else if (command.equalsIgnoreCase("menu_select?ask=-201&reply=2")) {
                showShopWindow(player, 2, false);
            } else if (command.equalsIgnoreCase("menu_select?ask=-201&reply=3")) {
                showShopWindow(player, 3, false);
            } else if (command.equalsIgnoreCase("menu_select?ask=-201&reply=4")) {
                showShopWindow(player, 4, false);
            } else if (command.equalsIgnoreCase("menu_select?ask=-201&reply=5")) {
                showShopWindow(player, 5, false);
            } else if (command.equalsIgnoreCase("menu_select?ask=-201&reply=6")) {
                showShopWindow(player, 6, false);
            } else {
                super.onBypassFeedback(player, command);
            }
        }
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        String filename = "castle/mercmanager/mercmanager-no.htm";
        int condition = validateCondition(player);
        if (condition == COND_BUSY_BECAUSE_OF_SIEGE) {
            filename = "castle/mercmanager/mercmanager-busy.htm"; // Busy
            // because
            // of siege
        } else if (condition == COND_OWNER) {
            if (SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION) {
                if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN) {
                    filename = "castle/mercmanager/mercmanager_dawn.htm";
                } else if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK) {
                    filename = "castle/mercmanager/mercmanager_dusk.htm";
                } else {
                    filename = "castle/mercmanager/mercmanager.htm";
                }
            } else {
                filename = "castle/mercmanager/mercmanager_nohire.htm";
            }
        }
        player.sendPacket(new HtmlMessage(this, filename, val));
    }

    private int validateCondition(final Player player) {
        if (player.isGM()) {
            return COND_OWNER;
        }
        if (getCastle() != null && getCastle().getId() > 0) {
            if (player.getClan() != null) {
                if (getCastle().getSiegeEvent().isInProgress()) {
                    return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
                } else if (getCastle().getOwnerId() == player.getClanId() // Clan
                        // owns
                        // castle
                        && (player.getClanPrivileges() & Clan.CP_CS_MERCENARIES) == Clan.CP_CS_MERCENARIES) // has
                // merc
                // rights
                {
                    return COND_OWNER; // Owner
                }
            }
        }
        return COND_ALL_FALSE;
    }
}