package org.mmocore.gameserver.scripts.npc.model.residences.clanhall;


import org.mmocore.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import org.mmocore.gameserver.model.entity.events.objects.CTBTeamObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.List;

/**
 * @author VISTALL
 * @date 15:13/27.04.2011
 */
public class MatchMassTeleporterInstance extends NpcInstance {
    private final int _flagId;
    private long _timeout;

    public MatchMassTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        _flagId = template.getAIParams().getInteger("flag");
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        ClanHall clanHall = getClanHall();
        ClanHallTeamBattleEvent siegeEvent = clanHall.getSiegeEvent();

        if (_timeout > System.currentTimeMillis()) {
            showChatWindow(player, "residence2/clanhall/agit_mass_teleporter001.htm");
            return;
        }

        if (isInRangeZ(player, getInteractDistance(player))) {
            _timeout = System.currentTimeMillis() + 60000L;

            List<CTBTeamObject> locs = siegeEvent.getObjects(ClanHallTeamBattleEvent.TRYOUT_PART);

            CTBTeamObject object = locs.get(_flagId);
            if (object.getFlag() != null) {
                for (Player $player : World.getAroundPlayers(this, 400, 100)) {
                    $player.teleToLocation(Location.findPointToStay(object.getFlag(), 100, 125));
                }
            }
        }
    }
}
