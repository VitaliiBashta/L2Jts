package org.mmocore.gameserver.scripts.npc.model.residences.clanhall;


import org.mmocore.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import org.mmocore.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

import java.util.List;

/**
 * @author VISTALL
 * @date 8:22/06.05.2011
 * 35603
 */
public class RainbowCoordinatorInstance extends NpcInstance {
    public RainbowCoordinatorInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        ClanHall clanHall = getClanHall();
        ClanHallMiniGameEvent miniGameEvent = clanHall.getSiegeEvent();
        if (miniGameEvent == null) {
            return;
        }

        if (miniGameEvent.isArenaClosed()) {
            showChatWindow(player, "residence2/clanhall/game_manager003.htm");
            return;
        }

        List<CMGSiegeClanObject> siegeClans = miniGameEvent.getObjects(ClanHallMiniGameEvent.ATTACKERS);

        CMGSiegeClanObject siegeClan = miniGameEvent.getSiegeClan(ClanHallMiniGameEvent.ATTACKERS, player.getClan());
        if (siegeClan == null) {
            showChatWindow(player, "residence2/clanhall/game_manager014.htm");
            return;
        }

        if (siegeClan.getPlayers().isEmpty()) {
            Party party = player.getParty();
            if (party == null) {
                showChatWindow(player, player.isClanLeader() ? "residence2/clanhall/game_manager005.htm" : "residence2/clanhall/game_manager002.htm");
                return;
            }

            if (!player.isClanLeader()) {
                showChatWindow(player, "residence2/clanhall/game_manager004.htm");
                return;
            }

            if (party.getMemberCount() < 5) {
                showChatWindow(player, "residence2/clanhall/game_manager003.htm");
                return;
            }

            if (party.getGroupLeader() != player) {
                showChatWindow(player, "residence2/clanhall/game_manager006.htm");
                return;
            }

            for (Player member : party.getPartyMembers()) {
                if (member.getClan() != player.getClan()) {
                    showChatWindow(player, "residence2/clanhall/game_manager007.htm");
                    return;
                }
            }

            int index = siegeClans.indexOf(siegeClan);

            SpawnExObject spawnEx = miniGameEvent.getFirstObject("arena_" + index);

            Location loc = (Location) spawnEx.getSpawns().get(0).getCurrentSpawnRange();

            for (Player member : party.getPartyMembers()) {
                siegeClan.addPlayer(member.getObjectId());
                member.teleToLocation(Location.coordsRandomize(loc, 100, 200));
            }
        } else {
            showChatWindow(player, "residence2/clanhall/game_manager013.htm");
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "residence2/clanhall/game_manager001.htm");
    }
}
