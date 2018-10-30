package org.mmocore.gameserver.templates;

import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL
 */
public enum InstantZoneEntryType {
    SOLO {
        @Override
        public boolean canEnter(final Player player, final InstantZone instancedZone) {
            if (player.isInParty()) {
                player.sendPacket(SystemMsg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
                return false;
            }

            final SystemMsg msg = checkPlayer(player, instancedZone);
            if (msg != null) {
                if (msg.size() > 0) {
                    player.sendPacket(new SystemMessage(msg).addName(player));
                } else {
                    player.sendPacket(msg);
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean canReEnter(final Player player, final InstantZone instancedZone) {
            final SystemMsg msg = reCheckPlayer(player, instancedZone);
            if (msg != null) {
                if (msg.size() > 0) {
                    player.sendPacket(new SystemMessage(msg).addName(player));
                } else {
                    player.sendPacket(msg);
                }
                return false;
            }
            return true;
        }
    },
    PARTY {
        @Override
        public boolean canEnter(final Player player, final InstantZone instancedZone) {
            final Party party = player.getParty();
            if (party == null) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                return false;
            }
            if (!party.isLeader(player)) {
                player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
                return false;
            }
            if (party.getMemberCount() < instancedZone.getMinParty()) {
                player.sendPacket(new SystemMessage(SystemMsg.YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE).addNumber(instancedZone.getMinParty()));
                return false;
            }
            if (party.getMemberCount() > instancedZone.getMaxParty()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
                return false;
            }

            for (final Player member : party.getPartyMembers()) {
                if (!player.isInRange(member, 500)) {
                    party.broadCast(new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
                    return false;
                }

                final SystemMsg msg = checkPlayer(member, instancedZone);
                if (msg != null) {
                    if (msg.size() > 0) {
                        party.broadCast(new SystemMessage(msg).addName(member));
                    } else {
                        member.sendPacket(msg);
                    }
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canReEnter(final Player player, final InstantZone instanceZone) {
            final Party party = player.getParty();
            if (party == null) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                return false;
            }
            if (party.getMemberCount() > instanceZone.getMaxParty()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
                return false;
            }
            final SystemMsg msg = reCheckPlayer(player, instanceZone);
            if (msg != null) {
                if (msg.size() > 0) {
                    player.sendPacket(new SystemMessage(msg).addName(player));
                } else {
                    player.sendPacket(msg);
                }
                return false;
            }
            return true;
        }
    },
    COMMAND_CHANNEL {
        @Override
        public boolean canEnter(final Player player, final InstantZone instancedZone) {
            final Party party = player.getParty();
            if (party == null || party.getCommandChannel() == null) {
                player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL);
                return false;
            }
            final CommandChannel cc = party.getCommandChannel();
            if (cc.getMemberCount() < instancedZone.getMinParty()) {
                player.sendPacket(new SystemMessage(SystemMsg.YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE).addNumber(instancedZone.getMinParty()));
                return false;
            }
            if (cc.getMemberCount() > instancedZone.getMaxParty()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
                return false;
            }
            for (final Player member : cc) {
                if (!player.isInRange(member, 500)) {
                    cc.broadCast(new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
                    return false;
                }

                final SystemMsg msg = checkPlayer(member, instancedZone);
                if (msg != null) {
                    if (msg.size() > 0) {
                        cc.broadCast(new SystemMessage(msg).addName(member));
                    } else {
                        member.sendPacket(msg);
                    }
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canReEnter(final Player player, final InstantZone instanceZone) {
            final Party commparty = player.getParty();
            if (commparty == null || commparty.getCommandChannel() == null) {
                player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL);
                return false;
            }
            final CommandChannel cc = commparty.getCommandChannel();
            if (cc.getMemberCount() > instanceZone.getMaxParty()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
                return false;
            }
            final SystemMsg msg = reCheckPlayer(player, instanceZone);
            if (msg != null) {
                if (msg.size() > 0) {
                    player.sendPacket(new SystemMessage(msg).addName(player));
                } else {
                    player.sendPacket(msg);
                }
                return false;
            }
            return true;
        }
    },
    PARTY_OR_COMMAND_CHANNEL {
        @Override
        public boolean canEnter(Player player, InstantZone instancedZone) {
            Party party = player.getParty();
            if (party == null) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                return false;
            }
            if (party.getCommandChannel() == null) {
                if (!party.isLeader(player)) {
                    player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
                    return false;
                }

                if (party.getMemberCount() < instancedZone.getMinParty()) {
                    player.sendPacket(new SystemMessage(SystemMsg.YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE).addNumber(instancedZone.getMinParty()));
                    return false;
                }
                for (Player member : party.getPartyMembers()) {
                    if (!player.isInRange(member, 500)) {
                        party.broadCast(new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
                        return false;
                    }

                    SystemMsg msg = checkPlayer(member, instancedZone);
                    if (msg != null) {
                        if (msg.size() > 0)
                            party.broadCast(new SystemMessage(msg).addName(member));
                        else
                            member.sendPacket(msg);
                        return false;
                    }
                }
                return true;
            }

            CommandChannel cc = party.getCommandChannel();
            if (cc.getMemberCount() < instancedZone.getMinParty()) {
                player.sendPacket(new SystemMessage(SystemMsg.YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE).addNumber(instancedZone.getMinParty()));
                return false;
            }
            if (cc.getMemberCount() > instancedZone.getMaxParty()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
                return false;
            }
            for (Player member : cc) {
                if (!player.isInRange(member, 500)) {
                    cc.broadCast(new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
                    return false;
                }

                SystemMsg msg = checkPlayer(member, instancedZone);
                if (msg != null) {
                    if (msg.size() > 0)
                        cc.broadCast(new SystemMessage(msg).addName(member));
                    else
                        member.sendPacket(msg);
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canReEnter(final Player player, final InstantZone instanceZone) {
            final Party commparty = player.getParty();
            if (commparty == null) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                return false;
            }
            if (commparty.getCommandChannel() == null) {
                if (commparty.getMemberCount() > instanceZone.getMaxParty()) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
                    return false;
                }
            } else {
                final CommandChannel cc = commparty.getCommandChannel();
                if (cc.getMemberCount() > instanceZone.getMaxParty()) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
                    return false;
                }
            }

            final SystemMsg msg = reCheckPlayer(player, instanceZone);
            if (msg != null) {
                if (msg.size() > 0)
                    player.sendPacket(new SystemMessage(msg).addName(player));
                else
                    player.sendPacket(msg);
                return false;
            }

            return true;
        }
    };

    private static SystemMsg checkPlayer(final Player player, final InstantZone instancedZone) {
        if (player.getActiveReflection() != null) {
            return SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON;
        }

        if (player.getLevel() < instancedZone.getMinLevel() || player.getLevel() > instancedZone.getMaxLevel()) {
            return SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY;
        }

        if (player.isCursedWeaponEquipped() || player.isInFlyingTransform()) {
            return SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
        }

        if (InstantZoneHolder.getInstance().getMinutesToNextEntrance(instancedZone.getId(), player) > 0) {
            return SystemMsg.C1_MAY_NOT_REENTER_YET;
        }

        if (instancedZone.getRemovedItemId() > 0 && instancedZone.getRemovedItemNecessity() && ItemFunctions.getItemCount(player, instancedZone.getRemovedItemId()) < 1) {
            return SystemMsg.C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
        }

        if (instancedZone.getRequiredQuestId() > 0) {
            final QuestState qs = player.getQuestState(instancedZone.getRequiredQuestId());
            if (qs == null || qs.getState() != Quest.STARTED) {
                return SystemMsg.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
            }
        }

        return null;
    }

    private static SystemMsg reCheckPlayer(final Player player, final InstantZone instancedZone) {
        if (player.getActiveReflection() != null && player.getActiveReflection().getInstancedZone() != instancedZone) {
            return SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON;
        }

        if (player.getLevel() < instancedZone.getMinLevel() || player.getLevel() > instancedZone.getMaxLevel()) {
            return SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY;
        }

        if (player.isCursedWeaponEquipped() || player.isInFlyingTransform()) {
            return SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
        }

        if (instancedZone.getRequiredQuestId() > 0) {
            final QuestState qs = player.getQuestState(instancedZone.getRequiredQuestId());
            if (qs == null || qs.getState() != Quest.STARTED) {
                return SystemMsg.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
            }
        }

        return null;
    }

    public abstract boolean canEnter(Player player, InstantZone instancedZone);

    public abstract boolean canReEnter(Player player, InstantZone instancedZone);
}