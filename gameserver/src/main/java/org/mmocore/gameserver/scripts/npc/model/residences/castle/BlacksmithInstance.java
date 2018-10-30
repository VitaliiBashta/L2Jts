package org.mmocore.gameserver.scripts.npc.model.residences.castle;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.ValidateLocation;
import org.mmocore.gameserver.network.lineage.serverpackets.MyTargetSelected;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class BlacksmithInstance extends NpcInstance {
    protected static final int COND_ALL_FALSE = 0;
    protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    protected static final int COND_OWNER = 2;

    public BlacksmithInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (CastleManorManager.getInstance().isDisabled()) {
            HtmlMessage html = new HtmlMessage(this);
            html.setFile("npcdefault.htm");
            player.sendPacket(html);
            return;
        }

        int condition = validateCondition(player);
        if (condition <= COND_ALL_FALSE) {
            return;
        }

        if (condition == COND_BUSY_BECAUSE_OF_SIEGE) {
            return;
        }

        if (condition == COND_OWNER) {
            if (command.startsWith("Chat")) {
                int val = 0;
                try {
                    val = Integer.parseInt(command.substring(5));
                } catch (IndexOutOfBoundsException ioobe) {
                } catch (NumberFormatException nfe) {
                }
                showMessageWindow(player, val);
            } else {
                super.onBypassFeedback(player, command);
            }
        }
    }

    private void showMessageWindow(Player player, int val) {
        player.sendActionFailed();
        HtmlMessage html = new HtmlMessage(this);

        if (CastleManorManager.getInstance().isDisabled()) {
            html.setFile("npcdefault.htm");
            player.sendPacket(html);
            return;
        }

        String filename = "castle/blacksmith/castleblacksmith-no.htm";

        int condition = validateCondition(player);
        if (condition > COND_ALL_FALSE) {
            if (condition == COND_BUSY_BECAUSE_OF_SIEGE) {
                filename = "castle/blacksmith/castleblacksmith-busy.htm"; // Busy because of siege
            } else if (condition == COND_OWNER) {
                if (val == 0) {
                    filename = "castle/blacksmith/castleblacksmith.htm";
                } else {
                    filename = "castle/blacksmith/castleblacksmith-" + val + ".htm";
                }
            }
        }

        html.setFile(filename);
        html.replace("%castleid%", Integer.toString(getCastle().getId()));
        player.sendPacket(html);
    }

    protected int validateCondition(Player player) {
        if (player.isGM()) {
            return COND_OWNER;
        }
        final Castle castle = getCastle();
        if (castle != null && castle.getId() > 0) {
            if (player.getClan() != null) {
                if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress()) {
                    return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
                } else if (castle.getOwnerId() == player.getClanId() // Clan owns castle
                        && (player.getClanPrivileges() & Clan.CP_CS_MANOR_ADMIN) == Clan.CP_CS_MANOR_ADMIN) // has manor rights
                {
                    return COND_OWNER; // Owner
                }
            }
        }
        return COND_ALL_FALSE;
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (this != player.getTarget()) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
            player.sendPacket(new ValidateLocation(this));
        } else {
            player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
            if (!isInRangeZ(player, getInteractDistance(player))) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                player.sendActionFailed();
            } else {
                if (CastleManorManager.getInstance().isDisabled()) {
                    HtmlMessage html = new HtmlMessage(this);
                    html.setFile("npcdefault.htm");
                    player.sendPacket(html);
                } else {
                    showMessageWindow(player, 0);
                }
                player.sendActionFailed();
            }
        }
    }
}
