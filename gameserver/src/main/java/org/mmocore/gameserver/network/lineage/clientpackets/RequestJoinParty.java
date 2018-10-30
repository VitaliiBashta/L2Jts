package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.Request.L2RequestType;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.AskJoinParty;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunish.Punish;
import org.mmocore.gameserver.world.World;

public class RequestJoinParty extends L2GameClientPacket {
    private String _name;
    private int _itemDistribution;

    @Override
    protected void readImpl() {
        _name = readS(ServerConfig.CNAME_MAXLEN);
        _itemDistribution = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isProcessingRequest()) {
            activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        final Player target = World.getPlayer(_name);
        if (target == null || (target.getPlayerAccess().GodMode && target.isInvisible())) {
            activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            return;
        }

        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT) {
            if (target.getBotPunishComponent().isBeingPunished()) {
                if (target.getBotPunishComponent().getPlayerPunish().canJoinParty() && target.getBotPunishComponent().getBotPunishType() == Punish.PARTYBAN) {
                    target.getBotPunishComponent().endPunishment();
                } else {
                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED);
                    return;
                }
            }
            if (activeChar.getBotPunishComponent().isBeingPunished()) {
                if (activeChar.getBotPunishComponent().getPlayerPunish().canJoinParty() && activeChar.getBotPunishComponent().getBotPunishType() == Punish.PARTYBAN) {
                    activeChar.getBotPunishComponent().endPunishment();
                } else {
                    activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED);
                    return;
                }
            }
        }

        if (target == activeChar) {
            activeChar.sendPacket(SystemMsg.THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY);
            activeChar.sendPacket(SystemMsg.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            activeChar.sendActionFailed();
            return;
        }

        if (EventsConfig.TVTAllowJoinParty || EventsConfig.CFTAllowJoinParty) {
            if (activeChar.isInActivePvpEvent() && target.getTeam() != activeChar.getTeam()) {
                activeChar.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
                return;
            }
        }

        if (target.isBusy()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
            return;
        }

        final IBroadcastPacket problem = target.canJoinParty(activeChar, target);
        if (problem != null) {
            activeChar.sendPacket(problem);
            return;
        }

        if (activeChar.isInParty()) {
            if (activeChar.getParty().getMemberCount() >= Party.MAX_SIZE) {
                activeChar.sendPacket(SystemMsg.THE_PARTY_IS_FULL);
                return;
            }

            // Только Party Leader может приглашать новых членов
            if (AllSettingsConfig.PARTY_LEADER_ONLY_CAN_INVITE && !activeChar.getParty().isLeader(activeChar)) {
                activeChar.sendPacket(SystemMsg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
                return;
            }

            if (activeChar.getParty().isInDimensionalRift()) {
                activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestJoinParty.InDimensionalRift"));
                activeChar.sendActionFailed();
                return;
            }
        }

        new Request(L2RequestType.PARTY, activeChar, target).setTimeout(10000L).set("itemDistribution", _itemDistribution);

        target.sendPacket(new AskJoinParty(activeChar.getName(), _itemDistribution));
        activeChar.sendPacket(new SystemMessage(SystemMsg.C1_HAS_BEEN_INVITED_TO_THE_PARTY).addName(target));
    }
}