package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author Mangol
 */
public class TeleportCubeInstance extends NpcInstance {
    private static final Zone baium_epic = ReflectionUtils.getZone("[baium_epic]");
    private static final Zone sailren_epic = ReflectionUtils.getZone("[sailren_epic]");
    private static final Zone valakas_epic = ReflectionUtils.getZone("[valakas_epic]");

    public TeleportCubeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    private static void randomTeleportBaium(final Player player) {
        final int i0 = Rnd.get(3);
        if (i0 == 0) {
            player.teleToLocation(108784 + Rnd.get(100), 16000 + Rnd.get(100), -4928);
        } else if (i0 == 1) {
            player.teleToLocation(113824 + Rnd.get(100), 10448 + Rnd.get(100), -5164);
        } else {
            player.teleToLocation(115488 + Rnd.get(100), 22096 + Rnd.get(100), -5168);
        }
    }

    private static void randomTeleportSailren(final Player player) {
        if (Rnd.get(100) < 40) {
            player.teleToLocation(10610, -24035, -3676);
        } else if (Rnd.get(100) < 70) {
            player.teleToLocation(10703, -24041, -3673);
        } else {
            player.teleToLocation(10769, -24107, -3672);
        }
    }

    private static void randomTeleportValakas(final Player player) {
        final int i1 = 150037 + Rnd.get(500);
        final int i2 = -57720 + Rnd.get(500);
        player.teleToLocation(i1, i2, -2976);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("teleport_request_baium")) {
            randomTeleportBaium(player);
        } else if (command.startsWith("teleport_request_sailren")) {
            randomTeleportSailren(player);
        } else if (command.startsWith("teleport_request_valakas")) {
            randomTeleportValakas(player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (getNpcId() == 31759) {
            if (baium_epic.checkIfInZone(player)) {
                showChatWindow(player, "default/baium_cube.htm");
            } else if (sailren_epic.checkIfInZone(player)) {
                showChatWindow(player, "default/teleport_cube_sailren001.htm");
            } else if (valakas_epic.checkIfInZone(player)) {
                showChatWindow(player, "default/teleport_cube_valakas001.htm");
            } else {
                super.showChatWindow(player, val);
            }
        } else {
            super.showChatWindow(player, val);
        }
    }
}