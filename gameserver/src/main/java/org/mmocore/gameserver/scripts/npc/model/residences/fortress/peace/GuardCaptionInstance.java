package org.mmocore.gameserver.scripts.npc.model.residences.fortress.peace;

import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.DoorObject;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.npc.model.residences.fortress.FacilityManagerInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.List;

/**
 * @author VISTALL
 * @date 16:29/17.04.2011
 */
public class GuardCaptionInstance extends FacilityManagerInstance {
    public GuardCaptionInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        final Fortress fortress = getFortress();
        if ("defenceInfo".equalsIgnoreCase(command)) {
            if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE) {
                showChatWindow(player, "residence2/fortress/fortress_not_authorized.htm");
                return;
            }

            if (fortress.getContractState() != Fortress.CONTRACT_WITH_CASTLE) {
                showChatWindow(player, "residence2/fortress/fortress_supply_officer005.htm");
                return;
            }

            showChatWindow(player, "residence2/fortress/fortress_garrison002.htm", "%facility_0%", fortress.getFacilityLevel(Fortress.REINFORCE), "%facility_2%", fortress.getFacilityLevel(Fortress.DOOR_UPGRADE), "%facility_3%", fortress.getFacilityLevel(Fortress.DWARVENS), "%facility_4%", fortress.getFacilityLevel(Fortress.SCOUT));
        } else if ("defenceUp1".equalsIgnoreCase(command) || "defenceUp2".equalsIgnoreCase(command)) {
            buyFacility(player, Fortress.REINFORCE, Integer.parseInt(command.substring(9, 10)), 100000);
        } else if ("deployScouts".equalsIgnoreCase(command)) {
            buyFacility(player, Fortress.SCOUT, 1, 150000);
        } else if ("doorUpgrade".equalsIgnoreCase(command)) {
            final boolean buy = buyFacility(player, Fortress.DOOR_UPGRADE, 1, 200000);
            if (buy) {
                final List<DoorObject> doorObjects = fortress.getSiegeEvent().getObjects(FortressSiegeEvent.UPGRADEABLE_DOORS);
                for (final DoorObject d : doorObjects) {
                    d.setUpgradeValue(fortress.<SiegeEvent<?, ?>>getSiegeEvent(), (int) d.getDoor().getMaxHp() * fortress.getFacilityLevel(Fortress.DOOR_UPGRADE));
                }
            }
        } else if ("hireDwarves".equalsIgnoreCase(command)) {
            buyFacility(player, Fortress.DWARVENS, 1, 100000);
        }
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        showChatWindow(player, "residence2/fortress/fortress_garrison001.htm");
    }
}
