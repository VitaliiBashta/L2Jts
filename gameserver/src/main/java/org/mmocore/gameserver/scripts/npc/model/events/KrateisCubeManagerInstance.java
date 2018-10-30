package org.mmocore.gameserver.scripts.npc.model.events;


import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.KrateisCubeEvent;
import org.mmocore.gameserver.model.entity.events.impl.KrateisCubeRunnerEvent;
import org.mmocore.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.List;
import java.util.StringTokenizer;

/**
 * @author VISTALL
 * @date 15:52/19.11.2010
 */
public class KrateisCubeManagerInstance extends NpcInstance {
    public KrateisCubeManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32503:
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc001.htm");
                break;
            default:
                super.showChatWindow(player, val, arg);
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("Kratei_UnRegister")) {
            KrateisCubeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 2);
            for (KrateisCubeEvent cubeEvent : runnerEvent.getCubes()) {
                List<KrateisCubePlayerObject> list = cubeEvent.getObjects(KrateisCubeEvent.REGISTERED_PLAYERS);
                KrateisCubePlayerObject krateisCubePlayer = cubeEvent.getRegisteredPlayer(player);

                if (krateisCubePlayer != null) {
                    list.remove(krateisCubePlayer);
                }
            }

            showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc012.htm");
        } else if (command.startsWith("Kratei_TryRegister")) {
            KrateisCubeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 2);
            if (runnerEvent.isRegistrationOver()) {
                if (runnerEvent.isInProgress()) {
                    showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc004.htm");
                } else {
                    showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc011.htm");
                }
            } else {
                if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10) {
                    showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc006.htm");
                    player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                } else if (player.isInParty()) {
                    showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc007.htm");
                } else if (player.getKarma() > 0 || player.getEffectList().getEffectByType(EffectType.Debuff) != null) {
                    showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc013.htm");
                } else if (player.getLevel() < 70) {
                    showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc010.htm");
                } else {
                    showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc003.htm");
                }
            }
        } else if (command.startsWith("Kratei_SeeList")) {
            if (player.getLevel() < 70) {
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc010.htm");
            } else {
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc003.htm");
            }
        } else if (command.startsWith("Kratei_Register")) {
            if (Olympiad.isRegistered(player) || HandysBlockCheckerManager.isRegistered(player)) {
                player.sendPacket(SystemMsg.YOU_CANNOT_BE_SIMULTANEOUSLY_REGISTERED_FOR_PVP_MATCHES_SUCH_AS_THE_OLYMPIAD_UNDERGROUND_COLISEUM_AERIAL_CLEFT_KRATEIS_CUBE_AND_HANDYS_BLOCK_CHECKERS);
                return;
            }

            if (player.isCursedWeaponEquipped()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON);
                return;
            }

            //TODO [VISTALL] Добавить проверки?

            StringTokenizer t = new StringTokenizer(command);
            if (t.countTokens() < 2) {
                return;
            }
            t.nextToken();
            KrateisCubeEvent cubeEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, Integer.parseInt(t.nextToken()));
            if (cubeEvent == null) {
                return;
            }

            if (player.getLevel() < cubeEvent.getMinLevel() || player.getLevel() > cubeEvent.getMaxLevel()) {
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc010.htm");
                return;
            }

            List<KrateisCubePlayerObject> list = cubeEvent.getObjects(KrateisCubeEvent.REGISTERED_PLAYERS);
            KrateisCubePlayerObject krateisCubePlayer = cubeEvent.getRegisteredPlayer(player);

            if (krateisCubePlayer != null) {
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc008.htm");
                return;
            }

            if (list.size() >= 25) {
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc005.htm");
            } else {
                cubeEvent.addObject(KrateisCubeEvent.REGISTERED_PLAYERS, new KrateisCubePlayerObject(player));
                showChatWindow(player, "pts/fantasy_island/kratei_cube/cratae_teleport_npc009.htm");
            }
        } else if (command.startsWith("teleportToFantasy")) {
            player.teleToLocation(-59716, -55920, -2048);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}