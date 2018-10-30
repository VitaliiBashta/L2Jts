package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.MoveToPawn;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.ValidateLocation;
import org.mmocore.gameserver.network.lineage.serverpackets.MyTargetSelected;
import org.mmocore.gameserver.network.lineage.serverpackets.StatusUpdate;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.WarehouseFunctions;

import java.util.StringTokenizer;

public final class NpcFriendInstance extends MerchantInstance {
    public NpcFriendInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    /**
     * this is called when a player interacts with this NPC
     *
     * @param player
     */
    @Override
    public void onAction(final Player player, final boolean shift) {
        if (this != player.getTarget()) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()), new ValidateLocation(this));
            if (isAutoAttackable(player)) {
                player.sendPacket(makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
            }
            player.sendActionFailed();
            return;
        }

        player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));

        if (shift && OnShiftActionHolder.getInstance().callShiftAction(player, NpcInstance.class, this, true)) {
            return;
        }

        if (isAutoAttackable(player)) {
            player.getAI().Attack(this, false, shift);
            return;
        }

        if (!isInRangeZ(player, getInteractDistance(player))) {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
            }
            return;
        }

        if (!AllSettingsConfig.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && !player.isGM()) {
            player.sendActionFailed();
            return;
        }

        // С NPC нельзя разговаривать мертвым, сидя и во время каста
        if (!AllSettingsConfig.ALLOW_TALK_WHILE_SITTING && player.isSitting() || player.isActionsDisabled()) {
            return;
        }

        player.sendActionFailed();
        player.sendPacket(new MoveToPawn(player, this, getInteractDistance(player)));
        if (!player.isSitting()) {
            player.setLastNpcInteractionTime();
        }
        String filename = "";

        if (getNpcId() >= 31370 && getNpcId() <= 31376 && player.getVarka() > 0 || getNpcId() >= 31377 && getNpcId() < 31384 && player.getKetra() > 0) {
            filename = "npc_friend/" + getNpcId() + "-nofriend.htm";
            showChatWindow(player, filename);
            return;
        }

        switch (getNpcId()) {
            case 31370:
            case 31371:
            case 31373:
            case 31377:
            case 31378:
            case 31380:
            case 31553:
            case 31554:
                filename = "npc_friend/" + getNpcId() + ".htm";
                break;
            case 31372:
                if (player.getKetra() > 2) {
                    filename = "npc_friend/" + getNpcId() + "-bufflist.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31379:
                if (player.getVarka() > 2) {
                    filename = "npc_friend/" + getNpcId() + "-bufflist.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31374:
                if (player.getKetra() > 1) {
                    filename = "npc_friend/" + getNpcId() + "-warehouse.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31381:
                if (player.getVarka() > 1) {
                    filename = "npc_friend/" + getNpcId() + "-warehouse.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31375:
                if (player.getKetra() == 3 || player.getKetra() == 4) {
                    filename = "npc_friend/" + getNpcId() + "-special1.htm";
                } else if (player.getKetra() == 5) {
                    filename = "npc_friend/" + getNpcId() + "-special2.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31382:
                if (player.getVarka() == 3 || player.getVarka() == 4) {
                    filename = "npc_friend/" + getNpcId() + "-special1.htm";
                } else if (player.getVarka() == 5) {
                    filename = "npc_friend/" + getNpcId() + "-special2.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31376:
                if (player.getKetra() == 4) {
                    filename = "npc_friend/" + getNpcId() + "-normal.htm";
                } else if (player.getKetra() == 5) {
                    filename = "npc_friend/" + getNpcId() + "-special.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31383:
                if (player.getVarka() == 4) {
                    filename = "npc_friend/" + getNpcId() + "-normal.htm";
                } else if (player.getVarka() == 5) {
                    filename = "npc_friend/" + getNpcId() + "-special.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31555:
                if (player.getRam() == 1) {
                    filename = "npc_friend/" + getNpcId() + "-special1.htm";
                } else if (player.getRam() == 2) {
                    filename = "npc_friend/" + getNpcId() + "-special2.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
                break;
            case 31556:
                if (player.getRam() == 2) {
                    filename = "npc_friend/" + getNpcId() + "-bufflist.htm";
                } else {
                    filename = "npc_friend/" + getNpcId() + ".htm";
                }
        }

        showChatWindow(player, filename);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken(); // Get actual command

        if ("Buff".equalsIgnoreCase(actualCommand)) {
            if (st.countTokens() < 1) {
                return;
            }
            int val = Integer.parseInt(st.nextToken());
            int item = 0;

            switch (getNpcId()) {
                case 31372:
                    item = 7186;
                    break;
                case 31379:
                    item = 7187;
                    break;
                case 31556:
                    item = 7251;
                    break;
            }

            int skill = 0;
            int level = 0;
            long count = 0;

            switch (val) {
                case 1:
                    skill = 4359;
                    level = 2;
                    count = 2;
                    break;
                case 2:
                    skill = 4360;
                    level = 2;
                    count = 2;
                    break;
                case 3:
                    skill = 4345;
                    level = 3;
                    count = 3;
                    break;
                case 4:
                    skill = 4355;
                    level = 2;
                    count = 3;
                    break;
                case 5:
                    skill = 4352;
                    level = 1;
                    count = 3;
                    break;
                case 6:
                    skill = 4354;
                    level = 3;
                    count = 3;
                    break;
                case 7:
                    skill = 4356;
                    level = 1;
                    count = 6;
                    break;
                case 8:
                    skill = 4357;
                    level = 2;
                    count = 6;
                    break;
            }

            if (skill != 0 && player.getInventory().destroyItemByItemId(item, count)) {
                player.doCast(SkillTable.getInstance().getSkillEntry(skill, level), player, true);
            } else {
                showChatWindow(player, "npc_friend/" + getNpcId() + "-havenotitems.htm");
            }
        } else if (command.startsWith("Chat")) {
            int val = Integer.parseInt(command.substring(5));
            String fname = "";
            fname = "npc_friend/" + getNpcId() + '-' + val + ".htm";
            if (fname != null && !fname.isEmpty()) {
                showChatWindow(player, fname);
            }
        } else if (command.startsWith("Buy")) {
            int val = Integer.parseInt(command.substring(4));
            showShopWindow(player, val, false);
        } else if (command.startsWith("WithdrawP")) {
            int val = Integer.parseInt(command.substring(10));
            if (val == 99) {
                HtmlMessage html = new HtmlMessage(this);
                html.setFile("npc-friend/personal.htm");
                html.replace("%npcname%", getName());
                player.sendPacket(html);
            } else {
                WarehouseFunctions.showRetrieveWindow(player, val);
            }
        } else if ("DepositP".equals(command)) {
            WarehouseFunctions.showDepositWindow(player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}