package org.mmocore.gameserver.scripts.npc.model.events;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.services.TeleToFantasyIsle;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;


/**
 * @author VISTALL
 * @date 23:26/16.06.2011
 */
public class UndergroundColiseumHelperInstance extends NpcInstance {
    private Location[][] LOCS = new Location[][]
            {
                    {new Location(-84451, -45452, -10728), new Location(-84580, -45587, -10728)},
                    {new Location(-86154, -50429, -10728), new Location(-86118, -50624, -10728)},
                    {new Location(-82009, -53652, -10728), new Location(-81802, -53665, -10728)},
                    {new Location(-77603, -50673, -10728), new Location(-77586, -50503, -10728)},
                    {new Location(-79186, -45644, -10728), new Location(-79309, -45561, -10728)}
            };

    public UndergroundColiseumHelperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equals("teleOut"))
            player.teleToLocation(TeleToFantasyIsle.POINTS[Rnd.get(TeleToFantasyIsle.POINTS.length)]);
        else if (command.startsWith("coliseum")) {
            int a = Integer.parseInt(String.valueOf(command.charAt(9)));
            Location[] locs = LOCS[a];

            player.teleToLocation(locs[Rnd.get(locs.length)]);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "events/guide_gcol001.htm");
    }
}