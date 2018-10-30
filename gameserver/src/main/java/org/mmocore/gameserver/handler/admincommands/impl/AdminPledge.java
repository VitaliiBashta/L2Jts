package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowInfoUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeStatusChanged;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.utils.Util;

import java.util.StringTokenizer;

/**
 * Pledge Manipulation //pledge <create|setlevel|resetcreate|resetwait|addrep|setleader>
 */
public class AdminPledge implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (activeChar.getPlayerAccess() == null || !activeChar.getPlayerAccess().CanEditPledge || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
            return false;
        }

        final Player target = (Player) activeChar.getTarget();

        if (fullString.startsWith("admin_pledge")) {
            final StringTokenizer st = new StringTokenizer(fullString);
            st.nextToken();

            final String action = st.nextToken(); // setlevel|resetcreate|resetwait|addrep

            switch (action) {
                case "create":
                    try {
                        if (target == null) {
                            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                            return false;
                        }
                        if (target.getPlayer().getLevel() < 10) {
                            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN);
                            return false;
                        }
                        final String pledgeName = st.nextToken();
                        if (pledgeName.length() > 16) {
                            activeChar.sendPacket(SystemMsg.CLAN_NAMES_LENGTH_IS_INCORRECT);
                            return false;
                        }
                        if (!Util.isMatchingRegexp(pledgeName, ServerConfig.CLAN_NAME_TEMPLATE)) {
                            // clan name is not matching template
                            activeChar.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID);
                            return false;
                        }

                        final Clan clan = ClanTable.getInstance().createClan(target, pledgeName);
                        if (clan != null) {
                            target.sendPacket(clan.listAll());
                            target.sendPacket(new PledgeShowInfoUpdate(clan), SystemMsg.YOUR_CLAN_HAS_BEEN_CREATED);
                            target.updatePledgeClass();
                            target.sendUserInfo(true);
                            return true;
                        } else {
                            activeChar.sendPacket(SystemMsg.THIS_NAME_ALREADY_EXISTS);
                            return false;
                        }
                    } catch (Exception e) {
                    }
                    break;
                case "setlevel":
                    if (target.getClan() == null) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }

                    try {
                        final int level = Integer.parseInt(st.nextToken());
                        final Clan clan = target.getClan();

                        activeChar.sendAdminMessage("You set level " + level + " for clan " + clan.getName());
                        clan.setLevel(level);
                        clan.updateClanInDB();

				/*	if(level < CastleSiegeManager.getSiegeClanMinLevel())
						SiegeUtils.removeSiegeSkills(target);
					else
						SiegeUtils.addSiegeSkills(target);   */

                        if (level == 5) {
                            target.sendPacket(SystemMsg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
                        }

                        final PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
                        final PledgeStatusChanged ps = new PledgeStatusChanged(clan);

                        for (final Player member : clan.getOnlineMembers(0)) {
                            member.updatePledgeClass();
                            member.sendPacket(SystemMsg.YOUR_CLANS_LEVEL_HAS_INCREASED, pu, ps);
                            member.broadcastUserInfo(true);
                        }

                        return true;
                    } catch (Exception e) {
                    }
                    break;
                case "resetcreate":
                    if (target.getClan() == null) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    target.getClan().setExpelledMemberTime(0);
                    activeChar.sendAdminMessage("The penalty for creating a clan has been lifted for " + target.getName());
                    break;
                case "resetwait":
                    target.setLeaveClanTime(0);
                    activeChar.sendAdminMessage("The penalty for leaving a clan has been lifted for " + target.getName());
                    break;
                case "addrep":
                    try {
                        final int rep = Integer.parseInt(st.nextToken());

                        if (target.getClan() == null || target.getClan().getLevel() < 5) {
                            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                            return false;
                        }
                        target.getClan().incReputation(rep, false, "admin_manual");
                        activeChar.sendAdminMessage("Added " + rep + " clan points to clan " + target.getClan().getName() + '.');
                    } catch (NumberFormatException nfe) {
                        activeChar.sendAdminMessage("Please specify a number of clan points to add.");
                    }
                    break;
                case "setleader":
                    final Clan clan = target.getClan();
                    if (target.getClan() == null) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    String newLeaderName = null;
                    if (st.hasMoreTokens()) {
                        newLeaderName = st.nextToken();
                    } else {
                        newLeaderName = target.getName();
                    }
                    final SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
                    final UnitMember newLeader = mainUnit.getUnitMember(newLeaderName);
                    if (newLeader == null) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    mainUnit.setLeader(newLeader, true);
                    clan.broadcastClanStatus(true, true, false);
                    break;
            }
        }

        return false;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_pledge
    }
}