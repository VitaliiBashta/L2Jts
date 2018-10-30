package org.mmocore.gameserver.scripts.npc.model.pts;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

/**
 * @author KilRoy
 */
public class FantasyIslandNpcInstance extends NpcInstance {
    private static final int[] MERCHANT = {32378};

    public FantasyIslandNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 4316: // Photo NPC
                showChatWindow(player, "pts/fantasy_island/g_photonpc001.htm");
                break;
            case 4317: // Photo NPC
                showChatWindow(player, "pts/fantasy_island/g_photonpc002.htm");
                break;
            case 4318: // Photo NPC
                showChatWindow(player, "pts/fantasy_island/g_photonpc003.htm");
                break;
            case 4319: // Photo NPC
                showChatWindow(player, "pts/fantasy_island/g_photonpc004.htm");
                break;
            case 4320: // Photo NPC
                showChatWindow(player, "pts/fantasy_island/g_photonpc005.htm");
                break;
            case 4321: // Photo NPC
                showChatWindow(player, "pts/fantasy_island/g_photonpc006.htm");
                break;
            case 4322: // Photo NPC
                showChatWindow(player, "pts/fantasy_island/g_photonpc007.htm");
                break;
            case 4323: // Photo NPC
                showChatWindow(player, "pts/fantasy_island/g_photonpc008.htm");
                break;
            case 32378: // tp_teleporter
                if (player.getKarma() > 0 || player.isCursedWeaponEquipped())
                    showChatWindow(player, "pts/fantasy_island/tp_teleporter003.htm");
                else
                    showChatWindow(player, "pts/fantasy_island/tp_teleporter001.htm");
                break;
            default:
                super.showChatWindow(player, val, arg);
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (player.getMountType() == 2) {
            return;
        }

        if (command.startsWith("reply_house")) {
            teleportPlayer(player, -57525, -54523, -1576);
        } else if (command.startsWith("reply_tunel_")) {
            if (command.endsWith("first")) {
                teleportPlayer(player, -58151, -53110, -1688);
            } else if (command.endsWith("second")) {
                teleportPlayer(player, -55223, -58832, -1680);
            }
        } else if (command.startsWith("reply_tuba_")) {
            if (command.endsWith("first")) {
                teleportPlayer(player, -55545, -56310, -1256);
            } else if (command.endsWith("second")) {
                teleportPlayer(player, -55646, -56314, -1296);
            } else if (command.endsWith("third")) {
                teleportPlayer(player, -55748, -56327, -1336);
            } else if (command.endsWith("mouth")) {
                teleportPlayer(player, -55355, -56305, -1112);
            }
        } else if (command.startsWith("reply_book_")) {
            if (command.endsWith("house")) {
                teleportPlayer(player, -59075, -59464, -1464);
            } else if (command.endsWith("castle")) {
                teleportPlayer(player, -61926, -59504, -1728);
            }
        } else if (command.startsWith("reply_clock_tower")) {
            teleportPlayer(player, -61288, -57736, -1600);
        } else if (command.startsWith("reply_paddies_")) {
            if (command.endsWith("coliseum")) {
                teleportPlayer(player, -81886, -48784, -10352);
            } else if (command.endsWith("kratei")) {
                teleportPlayer(player, -70411, -70958, -1416);
            } else if (command.endsWith("handy")) {
                teleportPlayer(player, -57328, -60566, -2360);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private void teleportPlayer(final Player player, final int x, final int y, final int z) {
        Location pos = Location.findPointToStay(x, y, z, 50, 100, player.getGeoIndex());
        player.teleToLocation(pos);
    }

    @Override
    public boolean isMerchantNpc() {
        if (ArrayUtils.contains(MERCHANT, getNpcId()))
            return true;
        return false;
    }
}