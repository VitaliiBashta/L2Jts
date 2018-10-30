package org.mmocore.gameserver.scripts.npc.model.pts.seven_sign;

import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

/**
 * @author KilRoy
 */
public class SSQNpcSSTeleporterInstance extends NpcInstance {
    private static final String TELEPORT_COORD = "teleportCoord";
    private static final String ENTER_GATEKEEPER = "enterType";
    private Location teleportLocation = new Location();
    private int onEnterType = 0;

    public SSQNpcSSTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        teleportLocation = Location.parseLoc(getParameter(TELEPORT_COORD, getSpawnedLoc().toXYZString()));
        onEnterType = getParameter(ENTER_GATEKEEPER, 0);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "pts/seven_sign/ss_teleporter001.htm");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("menu_select?ask=506&reply=1")) {
            if (onEnterType > 0) {
                int player_cabal = SevenSigns.getInstance().getPlayerCabal(player);
                int period = SevenSigns.getInstance().getCurrentPeriod();
                if (period == SevenSigns.PERIOD_COMPETITION && player_cabal == SevenSigns.CABAL_NULL) {
                    showChatWindow(player, "pts/seven_sign/ss_teleporter_q0506_01.htm");
                    //player.sendPacket(SystemMsg.USED_ONLY_DURING_A_QUEST_EVENT_PERIOD);
                    return;
                }

                int winner;
                if (period == SevenSigns.PERIOD_SEAL_VALIDATION && (winner = SevenSigns.getInstance().getCabalHighestScore()) != SevenSigns.CABAL_NULL) {
                    if (winner != player_cabal) {
                        showChatWindow(player, "pts/seven_sign/ss_teleporter_q0506_02.htm");
                        return;
                    }
                    if (onEnterType == 1 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) != player_cabal) {
                        showChatWindow(player, "pts/seven_sign/ss_teleporter_q0506_01.htm");
                        return;
                    }
                    if (onEnterType == 2 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) != player_cabal) {
                        showChatWindow(player, "pts/seven_sign/ss_teleporter_q0506_01.htm");
                        return;
                    }
                }
            }
            player.teleToLocation(teleportLocation);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}