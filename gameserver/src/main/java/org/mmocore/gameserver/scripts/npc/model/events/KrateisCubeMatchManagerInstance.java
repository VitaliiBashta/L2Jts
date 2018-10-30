package org.mmocore.gameserver.scripts.npc.model.events;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.events.impl.KrateisCubeEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

import java.util.List;


/**
 * @author VISTALL
 * @date 2:10 PM/21.11.2010
 */
public class KrateisCubeMatchManagerInstance extends NpcInstance {
    //private static final int[] SKILL_IDS = { 1086, 1204, 1059, 1085, 1078, 1068, 1240, 1077, 1242, 1062, 5739 };
    //private static final int[] SKILL_LEVEL = { 2, 2, 3, 3, 6, 3, 3, 3, 3, 2, 1 };

    public KrateisCubeMatchManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32504:
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc_70001.htm");
                break;
            case 32505:
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc_75001.htm");
                break;
            case 32506:
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc_80001.htm");
                break;
            default:
                super.showChatWindow(player, val, arg);
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        KrateisCubeEvent cubeEvent = player.getEvent(KrateisCubeEvent.class);
        if (cubeEvent == null) {
            return;
        }

        if (command.startsWith("KrateiEnter")) {
            if (!cubeEvent.isInProgress()) {
                switch (getNpcId()) {
                    case 32504:
                        showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc_70003.htm");
                        break;
                    case 32505:
                        showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc_75003.htm");
                        break;
                    case 32506:
                        showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc_80003.htm");
                        break;
                }
            } else {

                List<Location> locs = cubeEvent.getObjects(KrateisCubeEvent.TELEPORT_LOCS);

                player.teleToLocation(Rnd.get(locs), ReflectionManager.DEFAULT);
            }
        } else if (command.startsWith("KrateiExit")) {
            cubeEvent.exitCube(player, true);
        }
    }
}