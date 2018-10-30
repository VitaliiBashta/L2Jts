package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.manager.naia.NaiaTowerManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.ai.hellbound.NaiaLock;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public class NaiaControllerInstance extends NpcInstance {
    public NaiaControllerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("tryenter")) {
            if (NaiaLock.isEntranceActive()) {
                final Party party = player.getParty();
                if (party == null) {
                    player.sendPacket(SystemMsg.YOU_MUST_BE_IN_A_PARTY_IN_ORDER_TO_OPERATE_THE_MACHINE);
                    return;
                }

                if (!party.isLeader(player)) {
                    player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
                    return;
                }

                if (NaiaTowerManager.isValidParty(party)) {
                    _log.warn("NaiaControllerInstance: party {} already exist is naia manager.", player.toString());
                    return;
                }

                if (party.getCommandChannel() != null && AllSettingsConfig.naiaTeleportWholeCc) {
                    for (Party pt : party.getCommandChannel().getParties())
                        if (!checkParty(player, pt))
                            return;
                } else if (!checkParty(player, party))
                    return;

                NaiaTowerManager.enter(player);

                broadcastPacket(new MagicSkillUse(this, this, 5527, 1, 0, 0));
                doDie(null);
            } else {
                broadcastPacket(new MagicSkillUse(this, this, 5527, 1, 0, 0));
                doDie(null);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private boolean checkParty(Player player, Party party) {
        for (final Player member : party.getPartyMembers()) {
            if (member.getLevel() < 80) {
                player.sendPacket(new SystemMessage(SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY).addName(member));
                return false;
            }

            if (!member.isInRange(this, 500)) {
                player.sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
                return false;
            }
        }
        return true;
    }
}